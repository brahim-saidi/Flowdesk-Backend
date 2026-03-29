package com.hahnSoftware.ticket.repository;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.hahnSoftware.ticket.entity.Ticket;

import jakarta.persistence.criteria.JoinType;

public final class TicketSpecs {

    private TicketSpecs() {}

    public static Specification<Ticket> titleContains(String raw) {
        if (!StringUtils.hasText(raw)) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + raw.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<Ticket> createdAtFrom(Instant from) {
        if (from == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        Timestamp ts = Timestamp.from(from);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), ts);
    }

    public static Specification<Ticket> createdAtTo(Instant to) {
        if (to == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        Timestamp ts = Timestamp.from(to);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), ts);
    }

    public static Specification<Ticket> assigneeUserId(Long assigneeUserId) {
        if (assigneeUserId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            var assigned = root.join("assignedUser", JoinType.INNER);
            return cb.equal(assigned.get("userId"), assigneeUserId);
        };
    }

    public static Specification<Ticket> visibleToEmployee(Long userId) {
        return (root, query, cb) -> {
            var created = root.join("createdBy", JoinType.INNER);
            var assigned = root.join("assignedUser", JoinType.LEFT);
            return cb.or(
                    cb.equal(created.get("userId"), userId),
                    cb.and(
                            cb.isNotNull(assigned.get("userId")),
                            cb.equal(assigned.get("userId"), userId)));
        };
    }
}
