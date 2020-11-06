package com.sei.tamias.core.listener

import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.dsl.Disruptor
import com.sei.tamias.core.global.WatchEventContext
import java.nio.file.Path

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-11-04 19:58
 * @description :
 */
class PathDisruptor<EVENT>(var disruptor: Disruptor<EVENT>, var eventFactory: EventFactory<EVENT>) {

}
