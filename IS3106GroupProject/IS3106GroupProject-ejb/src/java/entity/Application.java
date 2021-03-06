/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Picture attribute maybe Path or smth
    private String companyName;
    private String caption;
    private int influencerRank;
    private String accepted;
    private String rejectReason;
    //Influencer
    private Long postId;
    private Long influencerId;
    private String influencerUsername;

    public Application() {
    }

    public Application(String companyName, String caption, int influencerRank, String accepted, String rejectReason) {
        this();
        this.companyName = companyName;
        this.caption = caption;
        this.influencerRank = influencerRank;
        this.accepted = accepted;
        this.rejectReason = rejectReason;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getInfluencerRank() {
        return influencerRank;
    }

    public void setInfluencerRank(int influencerRank) {
        this.influencerRank = influencerRank;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getInfluencerId() {
        return influencerId;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Application)) {
            return false;
        }
        Application other = (Application) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Application[ id=" + id + " ]";
    }

    public String getInfluencerUsername() {
        return influencerUsername;
    }

    public void setInfluencerUsername(String influencerUsername) {
        this.influencerUsername = influencerUsername;
    }
}
