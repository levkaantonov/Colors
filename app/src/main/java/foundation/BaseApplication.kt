package foundation

import foundation.model.Repository

interface BaseApplication {

    val repositories: List<Repository>
}