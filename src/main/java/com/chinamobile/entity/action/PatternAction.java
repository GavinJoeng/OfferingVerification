package com.chinamobile.entity.action;

import lombok.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 15:44
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class PatternAction<T> {
    private T actionType;

}


