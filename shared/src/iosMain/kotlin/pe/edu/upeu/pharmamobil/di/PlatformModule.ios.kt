package pe.edu.upeu.pharmamobil.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.data.local.CreadorDeDriver
import pe.edu.upeu.pharmamobil.data.local.CreadorDeDriverIos
import pe.edu.upeu.pharmamobil.platform.CompartidorIos

actual val platformModule: Module = module {
    single<Compartidor> { CompartidorIos() }
    single<CreadorDeDriver> { CreadorDeDriverIos() }
}
