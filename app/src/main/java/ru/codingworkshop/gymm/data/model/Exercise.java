package ru.codingworkshop.gymm.data.model;

import android.os.Parcelable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.requery.Convert;
import io.requery.Converter;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.JunctionTable;
import io.requery.Key;
import io.requery.Lazy;
import io.requery.ManyToMany;
import io.requery.ManyToOne;
import io.requery.Nullable;
import io.requery.Persistable;

/**
 * Created by Радик on 19.04.2017.
 */

@Entity
public interface Exercise extends Persistable, Parcelable {
    @Key
    @Generated
    long getId();

    String getName();
    void setName(String name);

    boolean getIsWithWeight();
    void setIsWithWeight(boolean isWithWeight);

    int getDifficulty();
    void setDifficulty(int difficulty);

    @Nullable
    String getYouTubeVideo();
    void setYouTubeVideo(String youTubeVideo);

    @Lazy
    @Nullable
    @Convert(ListConverter.class)
    String getSteps();
    void setSteps(String steps);

    @Lazy
    @Nullable
    @Convert(ListConverter.class)
    String getAdvices();
    void setAdvices(String advices);

    @Lazy
    @Nullable
    @Convert(ListConverter.class)
    String getCaution();
    void setCaution(String caution);

    @Lazy
    @Nullable
    @Convert(ListConverter.class)
    String getVariations();
    void setVariations(String variations);

    @Lazy
    @ManyToOne
    MuscleGroup getPrimaryMuscleGroup();
    void setPrimaryMuscleGroup(MuscleGroup primaryMuscleGroup);

    @Lazy
    @ManyToMany
    @JunctionTable
    List<MuscleGroup> getSecondaryMuscleGroups();
    void setSecondaryMuscleGroups(List<? extends MuscleGroup> secondaryMuscleGroups);

    final class ListConverter implements Converter<String, String> {

        @Override
        public Class<String> getMappedType() {
            return String.class;
        }

        @Override
        public Class<String> getPersistedType() {
            return String.class;
        }

        @Nullable
        @Override
        public Integer getPersistedSize() {
            return null;
        }

        @Override
        public String convertToPersisted(String value) {
            return value;
        }

        @Override
        public String convertToMapped(Class<? extends String> type, String value) {
            if (value == null || value.length() == 0)
                return value;

            Pattern pattern = Pattern.compile("^+", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(value);
            return matcher.replaceAll("\u2022 ");
        }
    }
}
