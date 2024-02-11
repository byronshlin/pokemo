package com.byronlin.pokemo.test

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Callable


class RxSchedulerRule : TestRule {
    private val SCHEDULER_INSTANCE: Scheduler = Schedulers.trampoline()

    private val schedulerFunction: Function<Scheduler, Scheduler> = object :
        Function<Scheduler, Scheduler> {
        @Throws(Exception::class)
        override fun apply(t: Scheduler): Scheduler {
            return SCHEDULER_INSTANCE
        }
    }

    private val schedulerFunctionLazy: Function<Callable<Scheduler>, Scheduler> =
        object : Function<Callable<Scheduler>, Scheduler> {
            override fun apply(t: Callable<Scheduler>): Scheduler {
                return SCHEDULER_INSTANCE
            }
        }

    override fun apply(base: Statement, description: Description?): Statement? {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerFunctionLazy)
                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler(schedulerFunction)
                RxJavaPlugins.setNewThreadSchedulerHandler(schedulerFunction)
                RxJavaPlugins.setComputationSchedulerHandler(schedulerFunction)
                base.evaluate()
                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}
