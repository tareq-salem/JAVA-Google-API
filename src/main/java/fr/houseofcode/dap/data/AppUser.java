/**
 *
 */
package fr.houseofcode.dap.data;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @author adminHOC
 *
 */
@Entity
public class AppUser {
    /**
     *
     */
    @GeneratedValue
    @Id
    private Integer id;
    /**
     *
     */
    private String userKey;

    /**
     *
     */
    @OneToMany
    private List<GoogleUser> guser;

    /**
     *
     */
    protected AppUser() {
    }

    /**
     *
     * @param user user
     */
    public AppUser(final String user) {
        this.userKey = user;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AppUser [id=").append(id).append(", userKey=").append(userKey).append("]");
        return builder.toString();
    }

}
