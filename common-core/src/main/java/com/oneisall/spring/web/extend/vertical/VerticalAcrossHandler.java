package com.oneisall.spring.web.extend.vertical;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.vertical.converter.*;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liuzhicong
 **/
@Slf4j
public class VerticalAcrossHandler<T extends VerticalTableRecord, K extends AcrossVoEntity> {

    private static final String MUTATOR_PREFIX = "set";

    private final Class<K> acrossClazz;

    private final Constructor<K> acrossConstructor;

    private final List<FieldConvertInfo> fieldInfoList;

    @ToString
    private static class FieldConvertInfo {
        private String fieldName;
        private Field field;
        private VerticalAcrossMapping mapping;
        private Class<?> fieldClazz;
        private String setterMethodName;
        private Method setterMethod;
        private VerticalConverter<?> converter;

        private static final Map<String, VerticalConverter<?>> converterMap = Maps.newConcurrentMap();

        private static final Map<Class<?>, Class<? extends VerticalConverter<?>>> defaultMappingConverterMap = Maps.newHashMap();

        static {
            defaultMappingConverterMap.put(String.class, StringConverter.class);
            defaultMappingConverterMap.put(Integer.class, IntConverter.class);
            defaultMappingConverterMap.put(BigDecimal.class, BigDecimalConverter.class);
            defaultMappingConverterMap.put(Date.class, DateConverter.class);
        }

        @SuppressWarnings("rawtypes")
        public static FieldConvertInfo of(Map<String, Method> maybeSetterMethodMap, Field field, VerticalAcrossMapping mapping) {
            FieldConvertInfo info = new FieldConvertInfo();
            String fieldName = field.getName();
            info.fieldName = fieldName;
            info.field = field;
            info.mapping = mapping;
            info.setterMethodName = MUTATOR_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            info.fieldClazz = field.getType();
            Method method = maybeSetterMethodMap.get(info.setterMethodName);
            if (method == null) {
                return null;
            } else {
                info.setterMethod = method;
            }
            try {
                Class<? extends VerticalConverter> converterClazz;
                if (mapping.usingConverter() != DefaultConverter.class) {
                    converterClazz = mapping.usingConverter();
                } else {
                    converterClazz = findConverter(info.fieldClazz, info.field);
                }
                String clazzName = converterClazz.getName();
                if (converterMap.get(clazzName) == null) {
                    info.converter = converterClazz.getDeclaredConstructor().newInstance();
                    converterMap.put(clazzName, info.converter);
                } else {
                    info.converter = converterMap.get(clazzName);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("无法为字段={}实例化转换器", fieldName, e);
                return null;
            }
            return info;
        }

        @SuppressWarnings("rawtypes")
        private static Class<? extends VerticalConverter> findConverter(Class<?> fieldClazz, Field field) {
            Class<? extends VerticalConverter<?>> result = defaultMappingConverterMap.get(fieldClazz);
            if (result == null && fieldClazz.isAssignableFrom(List.class)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length != 1) {
                        throw new IllegalArgumentException("自动匹配List转换器失败，泛型个数不为1");
                    }
                    Class<?> actualTypeArgument = (Class<?>) actualTypeArguments[0];
                    result = defaultMappingConverterMap.get(actualTypeArgument);
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("自动匹配List转换器失败，不支持的类型");
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public VerticalAcrossHandler() {
        // 获取泛型的实际类型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        acrossClazz = (Class<K>) actualTypeArguments[1];

        try {
            acrossConstructor = acrossClazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("无法获取类=" + acrossClazz.getName() + "的默认构造器");
        }

        // 初始化相关缓存
        Field[] declaredFields = acrossClazz.getDeclaredFields();
        Map<String, Method> maybeSetterMethodMap = Arrays.stream(acrossClazz.getDeclaredMethods())
                .filter(method -> {
                            if (!method.getName().startsWith(MUTATOR_PREFIX)) {
                                // 需要set开头
                                return false;
                            }
                            if (Modifier.isStatic(method.getModifiers())
                                    || method.isSynthetic() || method.isBridge()) {
                                // 排序静态等方法
                                return false;
                            }
                            // 参数必须是一个
                            int count = method.getParameterTypes().length;
                            return (count == 1);
                        }
                )
                .collect(Collectors.toMap(Method::getName, Function.identity()));
        fieldInfoList = Lists.newArrayListWithExpectedSize(declaredFields.length);
        for (Field declaredField : declaredFields) {
            VerticalAcrossMapping verticalAcrossMapping = declaredField.getAnnotation(VerticalAcrossMapping.class);
            if (verticalAcrossMapping == null) {
                continue;
            }
            // 需要映射的字段
            FieldConvertInfo fieldConvertInfo = FieldConvertInfo.of(maybeSetterMethodMap, declaredField, verticalAcrossMapping);
            if (fieldConvertInfo == null) {
                log.error("找不到字段转换信息，字段={}", declaredField.getName());
            }
            fieldInfoList.add(fieldConvertInfo);
        }
        log.debug("fieldInfoList size={}", fieldInfoList.size());
    }


    public List<K> convertToList(List<T> source) {
        if (CollectionMapUtil.isEmpty(source)) {
            return Lists.newArrayList();
        }
        Map<String, List<T>> primaryGroupMap = source.stream()
                .collect(Collectors.groupingBy(VerticalTableRecord::primaryGroupKey));

        LinkedList<K> result = Lists.newLinkedList();

        for (Map.Entry<String, List<T>> entry : primaryGroupMap.entrySet()) {
            String primaryKey = entry.getKey();
            List<T> primaryGroupTableList = entry.getValue();
            K vo = convert(primaryKey, primaryGroupTableList);
            result.add(vo);
        }
        return result;
    }

    public K convertToOne(List<T> source) {
        return convert("", source);
    }

    @SneakyThrows
    private K convert(String primaryKey, List<T> primaryGroupTableList) {
        K vo = acrossConstructor.newInstance();
        vo.setPrimaryGroupKey(primaryKey);
        if (CollectionMapUtil.isEmpty(primaryGroupTableList)) {
            return vo;
        }
        Map<String, List<T>> secondaryGroupMap = primaryGroupTableList.stream()
                .collect(Collectors.groupingBy(VerticalTableRecord::secondaryGroupKey));

        for (FieldConvertInfo fieldConvertInfo : fieldInfoList) {
            List<T> secondaryGroupTableList = secondaryGroupMap.get(fieldConvertInfo.mapping.value());
            if (CollectionMapUtil.isEmpty(secondaryGroupTableList)) {
                continue;
            }
            setData(vo, fieldConvertInfo, secondaryGroupTableList);
        }
        return vo;
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked"})
    private void setData(K vo, FieldConvertInfo fieldConvertInfo, List<T> secondaryGroupTableList) {
        VerticalConverter<?> converter = fieldConvertInfo.converter;
        Method setterMethod = fieldConvertInfo.setterMethod;
        VerticalAcrossMapping mapping = fieldConvertInfo.mapping;
        int size = secondaryGroupTableList.size();
        Object arg;
        if (fieldConvertInfo.fieldClazz.isAssignableFrom(List.class)) {
            ArrayList<Object> list = Lists.newArrayListWithCapacity(size);
            for (VerticalTableRecord verticalTableRecord : secondaryGroupTableList) {
                list.add(converter.convert(verticalTableRecord.value(), mapping));
            }
            arg = list;
        } else {
            if (size > 1 && mapping.checkManyToOne()) {
                throw new IllegalArgumentException("found many record but mapping only one");
            }
            if (size > 1) {
                Comparator<T> comparator = Comparator.comparing(VerticalTableRecord::mapOneSortValue).reversed();
                secondaryGroupTableList.sort(comparator);
            }
            arg = converter.convert(secondaryGroupTableList.get(0).value(), mapping);
        }
        setterMethod.invoke(vo, arg);
    }

}
