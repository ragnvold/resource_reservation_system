package com.nparmenov.reservation_system.controllers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nparmenov.reservation_system.models.reservation.Reservation;
import com.nparmenov.reservation_system.services.ReservationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/")
public class ReserveController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reserves")
    @ResponseBody
    public Flux<Reservation> findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public Mono<Boolean> releaseResourceReserveById(@PathVariable("id") Integer id) {
        return reservationService.releaseReservationById(id);
    }

    @GetMapping("/search/{id}")
    public Mono<Reservation> searchReserveById(@PathVariable("id") Integer id) {
        return reservationService.searchReservationById(id);
    }

    @GetMapping("/search/nearest/{id}")
    public Mono<Timestamp> searchNearestAvailableTimeToReservation(@PathVariable("id") Integer id) {
        Mono<Reservation> reservation = reservationService.searchNearestAvailableTimeToReservation(id);

        return reservation.map(res -> res.reservationEnd());
    }

    @GetMapping("/search")
    public Flux<Reservation> searchReservationsByCriterion(@RequestParam Map<String, String> allRequestParams) {
        return reservationService.searchReservationsByCriterion(allRequestParams);
    }
    

    @GetMapping("/create")
    @ResponseBody
    public Mono<ResponseEntity<Mono<Integer>>> createReserve(
        @RequestParam(name = "reservationStart") String reservationStart, 
        @RequestParam(name = "reservationDuration") String reservationDuration, 
        @RequestParam(name = "reservationResourceId") String reservationResourceId, 
        @RequestParam(name = "reservationResourceOwnerId") String reservationResourceOwnerId
    ) {
        return Mono.just(
            ResponseEntity.status(HttpStatus.OK)
                .body(
                    reservationService.createReservation(
                        Long.parseLong(reservationStart),
                        Long.parseLong(reservationDuration),
                        Integer.parseInt(reservationResourceId),
                        Integer.parseInt(reservationResourceOwnerId)
                    )
                )
            );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("error test");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<String> handleSQLException(SQLException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    /*

    

    */
    
    /*@ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        String responseStructure = "{\"status\": %s, \"message\": \"%s\", \"stackTrace\": \"%s\"}";
        String responseString = String.format(responseStructure, HttpStatus.CONFLICT.value(), exception.getMessage(), Arrays.toString(exception.getStackTrace()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseString);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String responseStructure = "{\"status\": %s, \"message\": \"%s\", \"stackTrace\": \"%s\"}";
        String responseString = String.format(responseStructure, HttpStatus.NOT_FOUND.value(), exception.getMessage(), Arrays.toString(exception.getStackTrace()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseString);
    }

    @ExceptionHandler(UnexpectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUnexpectedException(UnexpectedException exception) {
        String responseStructure = "{\"status\": %s, \"message\": \"%s\", \"stackTrace\": \"%s\"}";
        String responseString = String.format(responseStructure, HttpStatus.NOT_FOUND.value(), exception.getMessage(), Arrays.toString(exception.getStackTrace()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
    }*/
}