package com.samsepiol.library.token.management;

@FunctionalInterface
public interface TokenUse<T> {
    /** The supplied array is ephemeral and wiped immediately after this callback returns. Do not retain it. */
    T use(char[] token);
}
