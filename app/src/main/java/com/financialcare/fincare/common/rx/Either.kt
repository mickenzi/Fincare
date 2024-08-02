package com.financialcare.fincare.common.rx

sealed interface Either<out A, out B> {
    data class Left<A>(val value: A) : Either<A, Nothing>
    data class Right<B>(val value: B) : Either<Nothing, B>

    fun <T> map(transform: (B) -> T): Either<A, T> {
        return when (this) {
            is Left<A> -> this
            is Right<B> -> Right(transform(this.value))
        }
    }

    fun <T> fold(left: (A) -> T, right: (B) -> T): T {
        return when (this) {
            is Left<A> -> left(this.value)
            is Right<B> -> right(this.value)
        }
    }
}