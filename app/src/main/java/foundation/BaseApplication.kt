package foundation

import foundation.model.Repository

interface BaseApplication {

    val singletonScopeDependencies: List<Any>
}