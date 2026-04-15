package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.PlatformService;

import java.util.Optional;
import java.util.List;

public interface ServiceRepository {

    PlatformService save(PlatformService service);

    Optional<PlatformService> findById(String id);

    boolean existsById(String id);

    List<PlatformService> findAll();
}