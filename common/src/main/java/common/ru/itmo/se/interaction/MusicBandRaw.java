package common.ru.itmo.se.interaction;

import common.ru.itmo.se.data.Coordinates;
import common.ru.itmo.se.data.MusicBand;
import common.ru.itmo.se.data.MusicGenre;
import common.ru.itmo.se.data.Studio;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MusicBandRaw implements Serializable {
    private String name;
    private Coordinates coordinates;
    private Long numberOfParticipants;
    private LocalDateTime establishmentDate;
    private MusicGenre musicGenre;
    private Studio studio;

    /**
     * This method is a custom implementation of the toString() method in MusicBand.
     * @return values of the music band parsed to String data type.
     */
    @Override
    public String toString() {
        return "MusicBand in raw form" +
                "\n   Name: " + name +
                "\n   Coordinates: " + coordinates +
                "\n   Number of participants: " + numberOfParticipants +
                "\n   Establishment date: " + establishmentDate +
                "\n   Musical genre: " + musicGenre +
                "\n   Studio: " + studio;
    }
    @Override
    public int hashCode() {
        return name.hashCode() +
               coordinates.hashCode() +
               numberOfParticipants.hashCode() +
               establishmentDate.hashCode() +
               musicGenre.hashCode() +
               studio.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MusicBand musicBand) {
            return name.equals(musicBand.getName()) &&
                    coordinates.equals(musicBand.getCoordinates()) &&
                    Objects.equals(numberOfParticipants, musicBand.getNumberOfParticipants()) &&
                    establishmentDate.isEqual(musicBand.getEstablishmentDate()) &&
                    musicGenre.equals(musicBand.getMusicGenre()) &&
                    studio.equals(musicBand.getStudio());
        }
        return false;
    }
}
