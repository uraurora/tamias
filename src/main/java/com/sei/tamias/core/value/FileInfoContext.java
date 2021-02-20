package com.sei.tamias.core.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2021-02-20 18:37
 * @description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoContext {
    private Path dir;
    private BasicFileAttributes attributes;
}
