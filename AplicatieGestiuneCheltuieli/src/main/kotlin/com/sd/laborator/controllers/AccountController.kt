package com.sd.laborator.controllers

import com.sd.laborator.services.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/app/gestiune")
class AccountController(private val accountService: AccountService) {

    @PostMapping("/register")
    fun register(@RequestBody payload: Map<String, String>): ResponseEntity<Any> {
        val username = payload["username"] ?: return ResponseEntity.badRequest().body("Lipseste username")
        val password = payload["password"] ?: return ResponseEntity.badRequest().body("Lipseste password")
        val budget = payload["budget"] ?: return ResponseEntity.badRequest().body("Lipseste bugetul")
        val account = accountService.registerAccount(username, password,  budget.toDouble())
        return if (account != null) {
            ResponseEntity.status(HttpStatus.CREATED).body(account)
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Usernameul este deja folosit")
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody payload: Map<String, String>, request: HttpServletRequest): ResponseEntity<Any> {
        val username = payload["username"] ?: return ResponseEntity.badRequest().body("Lipseste username")
        val password = payload["password"] ?: return ResponseEntity.badRequest().body("Lipseste password")
        return if (accountService.authenticate(username, password)) {
            val session = request.getSession(true)
            request.changeSessionId()
            session.setAttribute("loggedInUser", 1)
            ResponseEntity.status(HttpStatus.ACCEPTED).body(accountService.getAccount(username))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentiale invalide")
        }
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        request.session?.invalidate()
        return ResponseEntity.ok("Iesire reusita")
    }

    @GetMapping("/buget/{idMembru}")
    fun getBudget(@PathVariable idMembru: Int, request: HttpServletRequest): ResponseEntity<Any> {
        val session = request.getSession(false)
        if (session?.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }
        val budget = accountService.getBudgetForMembru(idMembru)
        return if (budget != null) {
            ResponseEntity.ok(mapOf("buget" to budget))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Membru inexistent")
        }
    }

    @PostMapping("/buget/adauga/{idMembru}")
    fun addBudget(
        @PathVariable idMembru: Int,
        @RequestBody payload: Map<String, String>,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        val session = request.getSession(false)
        if (session?.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare necesara")
        }
        val amountStr = payload["amount"] ?: return ResponseEntity.badRequest().body("Lipseste amount")
        val amount = amountStr.toDoubleOrNull() ?: return ResponseEntity.badRequest().body("Amount invalid")
        return if (accountService.addBudgetForMembru(idMembru, amount)) {
            ResponseEntity.ok("Buget adaugat cu succes")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Membru inexistent")
        }
    }
}