package com.nparmenov.reservation_system.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nparmenov.reservation_system.models.reservation.Reservation;
import com.nparmenov.reservation_system.models.reservation.ReservationMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReservationRepositoryForH2Impl implements ReservationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReservationMapper mapper;
    @Autowired
    private DataSource dataSource;

    @Override
    public Flux<Reservation> findAllReservations() {
        String selectAllQuery = "SELECT * FROM RESOURCE_RESERVE";

        try {
            return Flux.fromIterable(jdbcTemplate.query(selectAllQuery, mapper));
        } catch (Exception exception) {
            return Flux.error(exception);
        }
    }

    @Override
    public Mono<Boolean> releaseReservationById(Integer reservationId) {
        String deleteReservationByIdQuery = "DELETE FROM RESOURCE_RESERVE WHERE ID=?";

        try {
            Integer result = jdbcTemplate.update(deleteReservationByIdQuery, reservationId);
            Boolean releaseResult = (result == 0) ? false : true;
            return Mono.just(releaseResult);
        } catch (DataAccessException e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Reservation> searchReservationById(Integer reservationId) {
        String searchReservationByIdQuery = "SELECT * FROM RESOURCE_RESERVE WHERE ID=?";

        try {
            return Mono.justOrEmpty(jdbcTemplate.queryForObject(searchReservationByIdQuery, mapper, reservationId));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Integer> createReservation(
        Long reservationStart,
        Long reservationDuration,
        Integer reservationResourceId,
        Integer reservationResourceOwnerId
    ) {
        String insertReservationForUniqueTimeByResourceIdQuery = "INSERT INTO RESOURCE_RESERVE (RESERVE_START, RESERVE_END, RESOURCE_ID, USER_ID) SELECT ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS (SELECT * FROM RESOURCE_RESERVE WHERE RESOURCE_ID=? AND RESERVE_START<=? AND RESERVE_END>=?)";

        try (
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(insertReservationForUniqueTimeByResourceIdQuery, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setTimestamp(1, new Timestamp(reservationStart * 1000));
            preparedStatement.setTimestamp(2, new Timestamp(reservationStart * 1000 + reservationDuration * 1000));
            preparedStatement.setInt(3, reservationResourceId);
            preparedStatement.setInt(4, reservationResourceOwnerId);
            preparedStatement.setInt(5, reservationResourceId);
            preparedStatement.setTimestamp(6, new Timestamp(reservationStart * 1000 + reservationDuration * 1000));
            preparedStatement.setTimestamp(7, new Timestamp(reservationStart * 1000));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                return Mono.error(new SQLException("can't add new entry"));
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Mono.just(generatedKeys.getInt("ID"));
                }
            }
        } catch (Exception e) {
            return Mono.error(e);
        }

        return Mono.error(new Exception("Can't create new reservation"));
    }

    @Override
    public Mono<Reservation> searchNearestAvailableTimeToReservation(Integer resourceId) {
        String searchReservationWithLatestTimeAvailableByResourceIdQuery = "SELECT * FROM RESOURCE_RESERVE WHERE RESOURCE_ID=? ORDER BY RESERVE_END DESC LIMIT 1";
        
        try {
            return Mono.just(jdbcTemplate.queryForObject(searchReservationWithLatestTimeAvailableByResourceIdQuery, mapper, resourceId));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Flux<Reservation> searchReservationsByUserId(Integer userId) {
        String searchReservationByUserIdQuery = "SELECT * FROM RESOURCE_RESERVE WHERE USER_ID=?";

        try {
            return Flux.fromIterable(jdbcTemplate.query(searchReservationByUserIdQuery, mapper, userId));
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    @Override
    public Flux<Reservation> searchReservationsByResourceId(Integer resourceId) {
        String searchReservationByResourceIdQuery = "SELECT * FROM RESOURCE_RESERVE WHERE RESOURCE_ID=?";

        try {
            return Flux.fromIterable(jdbcTemplate.query(searchReservationByResourceIdQuery, mapper, resourceId));
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    // получение резервирований, который полностью входят в заданные временные рамки
    @Override
    public Flux<Reservation> searchReservationsByTimeFrame(Long reservationStart, Long reservationEnd) {
        String insertReservationForUniqueTimeByResourceIdQuery = "SELECT * FROM RESOURCE_RESERVE WHERE RESERVE_START>=? AND RESERVE_END<=?";

        try {
            Timestamp reservationStartTimestamp = new Timestamp(reservationStart * 1000);
            Timestamp reservationEndTimestamp = new Timestamp(reservationEnd * 1000);

            return Flux.fromIterable(
                jdbcTemplate.query(
                    insertReservationForUniqueTimeByResourceIdQuery,
                    mapper,
                    reservationStartTimestamp,
                    reservationEndTimestamp
                )
            );
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}