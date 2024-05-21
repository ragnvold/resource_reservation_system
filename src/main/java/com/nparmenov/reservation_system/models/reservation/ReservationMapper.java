package com.nparmenov.reservation_system.models.reservation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reserve = new Reservation(
            rs.getInt("ID"), 
            rs.getTimestamp("RESERVE_START"), 
            rs.getTimestamp("RESERVE_END"), 
            rs.getInt("RESOURCE_ID"), 
            rs.getInt("USER_ID")
        );

        return reserve;
    }
    
}
