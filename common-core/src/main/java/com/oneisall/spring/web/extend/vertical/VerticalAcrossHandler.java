package com.oneisall.spring.web.extend.vertical;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuzhicong
 **/
public class VerticalAcrossHandler<T extends VerticalTableRecord, K extends AcrossVoEntity> {

    private final Class<K> valueClazz;

    @SuppressWarnings("unchecked")
    public VerticalAcrossHandler() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        valueClazz = (Class<K>) actualTypeArguments[1];
    }


    public List<K> convert(List<T> source) {
        if (CollectionMapUtil.isEmpty(source)) {
            return Lists.newArrayList();
        }
        Map<String, List<VerticalTableRecord>> primaryGroupMap = source.stream()
                .collect(Collectors.groupingBy(VerticalTableRecord::primaryGroupKey));

        LinkedList<K> result = Lists.newLinkedList();

        for (Map.Entry<String, List<VerticalTableRecord>> entry : primaryGroupMap.entrySet()) {
            String primaryKey = entry.getKey();
            List<VerticalTableRecord> primaryGroupTableList = entry.getValue();
            K vo = of(primaryKey, primaryGroupTableList);
            result.add(vo);
        }
        return result;
    }

    @SneakyThrows
    @SuppressWarnings({"rawtypes", "unchecked"})
    private K of(String primaryKey, List<VerticalTableRecord> primaryGroupTableList) {
        K vo = valueClazz.newInstance();
        vo.setPrimaryGroupKey(primaryKey);
        if (CollectionMapUtil.isEmpty(primaryGroupTableList)) {
            return vo;
        }
        Map<String, List<VerticalTableRecord>> secondaryGroupMap = primaryGroupTableList.stream()
                .collect(Collectors.groupingBy(VerticalTableRecord::secondaryGroupKey));

        Field[] declaredFields = valueClazz.getDeclaredFields();
        Map<String, VerticalConverter<?>> converterMap = Maps.newHashMap();

        for (Field declaredField : declaredFields) {
            VerticalAcrossMapping verticalAcrossMapping = declaredField.getAnnotation(VerticalAcrossMapping.class);
            if (verticalAcrossMapping == null) {
                continue;
            }

            List<VerticalTableRecord> secondaryGroupTableList = secondaryGroupMap.get(verticalAcrossMapping.mappingSecondaryValue());
            if (CollectionMapUtil.isEmpty(secondaryGroupTableList)) {
                continue;
            }

            Class<? extends VerticalConverter> converterClazz = verticalAcrossMapping.usingConverter();
            String name = converterClazz.getName();
            VerticalConverter<?> converter;
            if (converterMap.get(name) == null) {
                converter = converterClazz.newInstance();
                converterMap.put(name, converter);
            } else {
                converter = converterMap.get(name);
            }

            Class<?> type = declaredField.getType();
            int size = secondaryGroupTableList.size();
            if (type.isAssignableFrom(List.class)) {
                ArrayList<Object> list = Lists.newArrayListWithCapacity(size);
                for (VerticalTableRecord verticalTableRecord : secondaryGroupTableList) {
                    list.add(converter.convert(verticalTableRecord.value(),verticalAcrossMapping));
                }
                declaredField.setAccessible(true);
                declaredField.set(vo, list);
            } else {
                if (size > 1 && verticalAcrossMapping.throwFoundManyButMappingOne()) {
                    throw new IllegalArgumentException("found many record but mapping only one");
                }
                declaredField.setAccessible(true);
                if (size == 1) {
                    declaredField.set(vo, converter.convert(secondaryGroupTableList.get(0).value(),verticalAcrossMapping));
                } else {
                    Comparator<VerticalTableRecord> comparator = Comparator.comparing(VerticalTableRecord::mapOneSortValue).reversed();
                    secondaryGroupTableList.sort(comparator);
                    declaredField.set(vo, converter.convert(secondaryGroupTableList.get(0).value(),verticalAcrossMapping));
                }
            }
        }
        return vo;
    }

}
