package com.hahnSoftware.ticket.dto;

import jakarta.validation.constraints.NotNull;

public record UserEnabledRequest(@NotNull Boolean enabled) {}
