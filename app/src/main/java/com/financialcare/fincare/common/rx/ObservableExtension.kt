package com.financialcare.fincare.common.rx

import io.reactivex.rxjava3.core.Observable

fun <A : Any, E> Observable<Either<E, A>>.filterRight(): Observable<A> {
    return this.flatMap { if (it is Either.Right<A>) Observable.just(it.value) else Observable.empty() }
}

fun <A, E : Any> Observable<Either<E, A>>.filterLeft(): Observable<E> {
    return this.flatMap { if (it is Either.Left<E>) Observable.just(it.value) else Observable.empty() }
}