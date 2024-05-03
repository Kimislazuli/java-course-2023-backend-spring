/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.domain.jooq.tables.pojos;

import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private OffsetDateTime lastUpdate;
    private OffsetDateTime lastCheck;

    public Link() {
    }

    public Link(Link value) {
        this.id = value.id;
        this.url = value.url;
        this.lastUpdate = value.lastUpdate;
        this.lastCheck = value.lastCheck;
    }

    @ConstructorProperties({"id", "url", "lastUpdate", "lastCheck"})
    public Link(
        @Nullable Long id,
        @NotNull String url,
        @Nullable OffsetDateTime lastUpdate,
        @Nullable OffsetDateTime lastCheck
    ) {
        this.id = id;
        this.url = url;
        this.lastUpdate = lastUpdate;
        this.lastCheck = lastCheck;
    }

    /**
     * Getter for <code>LINK.ID</code>.
     */
    @Nullable
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>LINK.ID</code>.
     */
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    /**
     * Getter for <code>LINK.URL</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for <code>LINK.URL</code>.
     */
    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    /**
     * Getter for <code>LINK.LAST_UPDATE</code>.
     */
    @Nullable
    public OffsetDateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Setter for <code>LINK.LAST_UPDATE</code>.
     */
    public void setLastUpdate(@Nullable OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Getter for <code>LINK.LAST_CHECK</code>.
     */
    @Nullable
    public OffsetDateTime getLastCheck() {
        return this.lastCheck;
    }

    /**
     * Setter for <code>LINK.LAST_CHECK</code>.
     */
    public void setLastCheck(@Nullable OffsetDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Link other = (Link) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!this.url.equals(other.url)) {
            return false;
        }
        if (this.lastUpdate == null) {
            if (other.lastUpdate != null) {
                return false;
            }
        } else if (!this.lastUpdate.equals(other.lastUpdate)) {
            return false;
        }
        if (this.lastCheck == null) {
            if (other.lastCheck != null) {
                return false;
            }
        } else if (!this.lastCheck.equals(other.lastCheck)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
        result = prime * result + ((this.lastUpdate == null) ? 0 : this.lastUpdate.hashCode());
        result = prime * result + ((this.lastCheck == null) ? 0 : this.lastCheck.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Link (");

        sb.append(id);
        sb.append(", ").append(url);
        sb.append(", ").append(lastUpdate);
        sb.append(", ").append(lastCheck);

        sb.append(")");
        return sb.toString();
    }
}
