package com.oneisall.spring.web.mongodb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
public class MongodbQuery {
    @NotBlank
    private String uri;

    @NotBlank
    private String clazz;

    @Valid
    private List<MongodbCondition> conditions;

    private List<String> sortBys;

    private Sort.Direction sortDirection;

    private Integer pageNumber = 1;

    private Integer pageSize = 15;

    @JsonIgnore
    public Class<?> findClass() throws ClassNotFoundException {
        return Class.forName(clazz);
    }
}
