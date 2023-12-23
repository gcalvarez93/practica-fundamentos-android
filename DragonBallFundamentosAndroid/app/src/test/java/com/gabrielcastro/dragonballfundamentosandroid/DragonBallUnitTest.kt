package com.gabrielcastro.dragonballfundamentosandroid

import com.gabrielcastro.dragonballfundamentosandroid.Home.Model.Heroe
import com.gabrielcastro.dragonballfundamentosandroid.Home.Model.HeroeDTO
import com.gabrielcastro.dragonballfundamentosandroid.Home.ViewModel.HomeViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DragonBallUnitTest {

    @get:Rule
    var mainCoroutineRule = com.gabrielcastro.dragonballfundamentosandroid.Rule.CoreCoroutineRule()

    val viewModel = HomeViewModel()
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun heroe_isCorrect(){
        val heroe = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1234",
            favorite = false,
            description = "Description",
            name = "Goku",
            //the remaining properties will get the default value
        )

        assertEquals(heroe.name,"Goku")
        assertNotSame(heroe.name, "Bulma")
        //val isValidURL = URLUtil.isValidUrl(heroe.photo)
        //val isValidURL2 = Patterns.WEB_URL.matcher(heroe.photo).matches();
        // val rowbytes = Picasso.get().load(heroe.photo).get().rowBytes
        // assertTrue(rowbytes > 0)
        assertTrue(URL(heroe.photo) is URL)
        assertTrue(heroe.id is String)
        assertFalse(heroe.favorite)
        assertEquals(heroe.description, "Description")
        assertFalse(heroe.isDead)
        assertEquals(heroe.currentHitPoints - heroe.totalHitPoints, 0)
        assertEquals(heroe.timesSlected,0)
    }

    @Test
    fun heroeDTO_isCorrect(){
        val heroe = HeroeDTO(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1234",
            favorite = false,
            description = "Description",
            name = "Goku",
            //the remaining properties will get the default value
        )

        assertEquals(heroe.name,"Goku")
        assertNotSame(heroe.name, "Bulma")
        //val isValidURL = URLUtil.isValidUrl(heroe.photo)
        //val isValidURL2 = Patterns.WEB_URL.matcher(heroe.photo).matches();
        // val rowbytes = Picasso.get().load(heroe.photo).get().rowBytes
        // assertTrue(rowbytes > 0)
        assertTrue(URL(heroe.photo) is URL)
        assertTrue(heroe.id is String)
        assertFalse(heroe.favorite)
        assertEquals(heroe.description, "Description")

    }


    @Test
    fun `coreViewModel attack and death check`() = runTest {
        viewModel.selectedHeroe = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1234",
            favorite = false,
            description = "Description",
            name = "Goku",
            //the remaining properties will get the default value
        )
        launch {
            viewModel.uiState.collect{
                when(it){
                    is HomeViewModel.UiStateCA.OnHeroIsDead -> {
                        assertTrue(viewModel.selectedHeroe.currentHitPoints <= 0)
                        cancel()
                    }
                    is HomeViewModel.UiStateCA.OnHeroeHPChange -> {
                        assertTrue(viewModel.selectedHeroe.currentHitPoints > 0)
                        val damageReceived = - (viewModel.selectedHeroe.totalHitPoints - viewModel.selectedHeroe.currentHitPoints)
                        assertTrue(damageReceived >= -60)
                        assertTrue(damageReceived <= -10)
                        cancel()
                    }
                    is HomeViewModel.UiStateCA.Idle ->
                        Unit
                    else -> {
                        cancel()
                    }
                }
            }
        }
        viewModel.fightOnClickMethod("attack")
    }

    @Test
    fun `coreViewModel selectedHeroe check`() = runTest {
        val heroe = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1234",
            favorite = false,
            description = "Description",
            name = "Goku",
            //the remaining properties will get the default value
        )
        launch {
            viewModel.uiState.collect{
                when(it){
                    is HomeViewModel.UiStateCA.OnHeroeSelectedToFight -> {
                        assertTrue(it.heroe.timesSlected > 0)
                        cancel()
                    }
                    else -> {
                        cancel()
                    }
                }
            }
        }
        viewModel.selectedHeroToFightClicked(heroe)
    }

}