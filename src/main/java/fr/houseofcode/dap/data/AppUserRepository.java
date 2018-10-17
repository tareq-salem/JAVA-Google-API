package fr.houseofcode.dap.data;

import org.springframework.data.repository.CrudRepository;

/**
 * @author adminHOC
 *
 */
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    /**
     *
     * @param userKey userKey
     * @return getByUserKey
     */
    public AppUser getByUserKey(String userKey);
}
