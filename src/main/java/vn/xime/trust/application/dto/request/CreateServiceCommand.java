package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class CreateServiceCommand {
    private final String id;
    private final String name;
    private final String tenant;
}