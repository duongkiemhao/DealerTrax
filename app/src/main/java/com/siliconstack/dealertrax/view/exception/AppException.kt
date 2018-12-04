package com.bol.instantapp.exception

open class AppException(val exceptin: Throwable?): Exception();

open class NoNetworkException(exceptinNoNetwork: Throwable?): AppException(exceptinNoNetwork);