package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {
    @Insert("INSERT INTO reports (user_id, card_id, message) " +
            "VALUES (#{user},#{card},#{message}) ON CONFLICT DO UPDATE")
    void addReport(Integer user, Integer card, String message);
}
