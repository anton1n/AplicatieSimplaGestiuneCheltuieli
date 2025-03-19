package com.sd.laborator.controllers

import com.sd.laborator.models.Cheltuiala
import com.sd.laborator.service.CheltuialaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/app/cheltuieli")
class CheltuialaController(private val cheltuialaService: CheltuialaService) {

    private fun isAuthenticated(request: HttpServletRequest): Boolean {
        val session = request.getSession(false)
        return session?.getAttribute("loggedInUser") != null
    }
    @PostMapping("/adauga")
    fun adaugaCheltuiala(@RequestBody cheltuiala: Cheltuiala, request: HttpServletRequest): ResponseEntity<Any> {
        return if(isAuthenticated(request))
        {
            val adaugata = cheltuialaService.adaugaCheltuiala(cheltuiala)
            ResponseEntity.status(HttpStatus.CREATED).body(adaugata)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }

    }

    @GetMapping("/membru/{idMembru}")
    fun getCheltuieliPentruMembru(@PathVariable idMembru: Int, request: HttpServletRequest): ResponseEntity<Any> //ResponseEntity<List<Cheltuiala>>
    {
        return if(isAuthenticated(request)) {
            val lista = cheltuialaService.getCheltuieliPentruMembru(idMembru)
            ResponseEntity.ok(lista)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }
    }

    @PutMapping("/actualizeaza/{id}")
    fun actualizeazaCheltuiala(@PathVariable id: Int, @RequestBody cheltuiala: Cheltuiala, request: HttpServletRequest): ResponseEntity<Any> {
        return if (isAuthenticated(request)) {
            val actualizata = cheltuialaService.actualizeazaCheltuiala(id, cheltuiala)
            return if (actualizata != null)
                ResponseEntity.ok(actualizata)
            else
                ResponseEntity.notFound().build()
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }
    }

    @DeleteMapping("/sterge/{id}")
    fun stergeCheltuiala(@PathVariable id: Int, request: HttpServletRequest): ResponseEntity<Any> {
        return if(isAuthenticated(request)) {
            cheltuialaService.stergeCheltuiala(id)
            return ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }
    }
}