package com.sei.tamias.db.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.sei.tamias.db.entity.UserEntity
import com.sei.tamias.db.mapper.UserMapper
import com.sei.tamias.db.service.UserService

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-10-28 23:21
 * @description :
 */
open class UserServiceImpl: ServiceImpl<UserMapper, UserEntity>(), UserService {

}