package com.sei.tamias.db.entity

import com.sei.tamias.global.nowDateTimeString

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-11-06 17:23
 * @description :
 */
open class BaseEntity(var createTime:String= nowDateTimeString(), var updateTime:String= nowDateTimeString()) {
}