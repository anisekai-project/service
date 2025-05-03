package me.anisekai.library.interfaces;

import java.io.File;

public interface FileProvider<T> {

    File getFileFrom(T source);

}
