package me.anisekai.toshiko.entities;

import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@Entity
public class Torrent {

    @Id
    private int     id;
    private long    eta;
    private long    addedDate;
    private String  downloadDir;
    private String  name;
    private boolean isFinished;
    private boolean isStalled;

    private long peersConnected;
    private long peersGettingFromUs;
    private long peersSendingToUs;

    private long queuePosition;

    private long       rateDownload;
    private long       rateUpload;
    private long       leftUntilDone;
    private long       metadataPercentComplete;
    private BigDecimal percentDone;
    private BigDecimal uploadRatio;
    private BigDecimal uploadedEver;

    private BigDecimal seedRatioLimit;
    private BigDecimal seedRatioMode;

    private long sizeWhenDone;

    public Torrent() {}

    public Torrent(JSONObject source) throws IllegalAccessException {

        Class<Torrent> self = Torrent.class;
        for (Field field : self.getDeclaredFields()) {
            // This is most likely true, but we never know.
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);

            if (field.getType().equals(Long.class)) {
                field.set(this, source.getLong(field.getName()));
            } else if (field.getType().equals(Integer.class)) {
                field.set(this, source.getInt(field.getName()));
            } else if (field.getType().equals(Boolean.class)) {
                field.set(this, source.getBoolean(field.getName()));
            } else if (field.getType().equals(BigDecimal.class)) {
                field.set(this, source.getBigDecimal(field.getName()));
            } else {
                field.set(this, source.get(field.getName()));
            }

            field.setAccessible(accessible);
        }
    }

    public int getId() {

        return this.id;
    }

    public long getEta() {

        return this.eta;
    }

    public String getName() {

        return this.name;
    }

    public long getAddedDate() {

        return this.addedDate;
    }

    public String getDownloadDir() {

        return this.downloadDir;
    }

    public boolean isFinished() {

        return this.isFinished;
    }

    public boolean isStalled() {

        return this.isStalled;
    }

    public long getPeersConnected() {

        return this.peersConnected;
    }

    public long getPeersGettingFromUs() {

        return this.peersGettingFromUs;
    }

    public long getPeersSendingToUs() {

        return this.peersSendingToUs;
    }

    public long getQueuePosition() {

        return this.queuePosition;
    }

    public long getRateDownload() {

        return this.rateDownload;
    }

    public long getRateUpload() {

        return this.rateUpload;
    }

    public long getLeftUntilDone() {

        return this.leftUntilDone;
    }

    public long getMetadataPercentComplete() {

        return this.metadataPercentComplete;
    }

    public BigDecimal getPercentDone() {

        return this.percentDone;
    }

    public BigDecimal getUploadRatio() {

        return this.uploadRatio;
    }

    public BigDecimal getUploadedEver() {

        return this.uploadedEver;
    }

    public BigDecimal getSeedRatioLimit() {

        return this.seedRatioLimit;
    }

    public BigDecimal getSeedRatioMode() {

        return this.seedRatioMode;
    }

    public long getSizeWhenDone() {

        return this.sizeWhenDone;
    }


}
