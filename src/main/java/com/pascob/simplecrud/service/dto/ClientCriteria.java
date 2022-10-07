package com.pascob.simplecrud.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.pascob.simplecrud.domain.Client} entity. This class is used
 * in {@link com.pascob.simplecrud.web.rest.ClientResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clients?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClientCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private StringFilter telephone;

    private DoubleFilter solde;

    private BooleanFilter isActif;

    private LocalDateFilter dateCreation;

    public ClientCriteria() {
    }

    public ClientCriteria(ClientCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.telephone = other.telephone == null ? null : other.telephone.copy();
        this.solde = other.solde == null ? null : other.solde.copy();
        this.isActif = other.isActif == null ? null : other.isActif.copy();
        this.dateCreation = other.dateCreation == null ? null : other.dateCreation.copy();
    }

    @Override
    public ClientCriteria copy() {
        return new ClientCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNom() {
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getTelephone() {
        return telephone;
    }

    public void setTelephone(StringFilter telephone) {
        this.telephone = telephone;
    }

    public DoubleFilter getSolde() {
        return solde;
    }

    public void setSolde(DoubleFilter solde) {
        this.solde = solde;
    }

    public BooleanFilter getIsActif() {
        return isActif;
    }

    public void setIsActif(BooleanFilter isActif) {
        this.isActif = isActif;
    }

    public LocalDateFilter getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateFilter dateCreation) {
        this.dateCreation = dateCreation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ClientCriteria that = (ClientCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(telephone, that.telephone) &&
            Objects.equals(solde, that.solde) &&
            Objects.equals(isActif, that.isActif) &&
            Objects.equals(dateCreation, that.dateCreation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        nom,
        telephone,
        solde,
        isActif,
        dateCreation
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (nom != null ? "nom=" + nom + ", " : "") +
                (telephone != null ? "telephone=" + telephone + ", " : "") +
                (solde != null ? "solde=" + solde + ", " : "") +
                (isActif != null ? "isActif=" + isActif + ", " : "") +
                (dateCreation != null ? "dateCreation=" + dateCreation + ", " : "") +
            "}";
    }

}
