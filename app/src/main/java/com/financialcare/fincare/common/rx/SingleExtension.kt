package com.financialcare.fincare.common.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

inline fun <A : Any, reified E> Single<A>.either(): Single<Either<E, A>> {
    return this
        .map<Either<E, A>> { Either.Right(it) }
        .onErrorResumeNext {
            if (it is E) {
                Single.just(Either.Left(it))
            } else {
                Single.fromObservable(Observable.empty())
            }
        }
}