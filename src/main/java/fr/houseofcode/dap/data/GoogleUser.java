/**
 *
 */
package fr.houseofcode.dap.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author adminHOC
 *
 */
@Entity
public class GoogleUser {

    /**
    *
    */
    @GeneratedValue
    @Id
    private Integer id;

    /**
    *
    */
    @ManyToOne
    private AppUser appUser;

    /**
    *
    */
    protected GoogleUser() {
    }

    /**
    *
    * @param user internal user
    */
    public GoogleUser(final AppUser user) {
        this.appUser = user;
    }

    /**
    *
    * @param id
    */
    public void setId(int id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GmailUser [id=").append(id).append(", appUser=").append(appUser).append("]");
        return builder.toString();
    }

}
