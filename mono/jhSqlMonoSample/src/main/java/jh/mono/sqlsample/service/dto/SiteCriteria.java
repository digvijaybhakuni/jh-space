package jh.mono.sqlsample.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the Site entity. This class is used in SiteResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /sites?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SiteCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private ZonedDateTimeFilter creationDateTime;

    private LongFilter systemId;

    public SiteCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ZonedDateTimeFilter getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTimeFilter creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LongFilter getSystemId() {
        return systemId;
    }

    public void setSystemId(LongFilter systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "SiteCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (creationDateTime != null ? "creationDateTime=" + creationDateTime + ", " : "") +
                (systemId != null ? "systemId=" + systemId + ", " : "") +
            "}";
    }

}
