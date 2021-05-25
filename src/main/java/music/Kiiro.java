package music;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.NoteName;
import music.theory.Pitch;

public class Kiiro {

    private static final Logger LOG = LoggerFactory.getLogger(Kiiro.class);


    private ChordType type = ChordType.MAJ7;

    public void kiir() {
        NoteName[] noteNames = NoteName.values();

        //        for(int i = 0; i < noteNames.length; i++) {
        //            Pitch[] pitches = Scale.majorScale(noteNames[i]);
        //            LOG.debug("*************************************************************");
        //            LOG.debug("scale: {}, pitches: {}", noteNames[i], Arrays.asList(pitches));
        //
        //            for(int j = 0; j < 7; j++) {
        //                LOG.debug("MAJ  degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.MAJ).getPitches()));
        //                LOG.debug("MIN  degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.MIN).getPitches()));
        //                LOG.debug("AUG  degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.AUG).getPitches()));
        //                LOG.debug("DIM  degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.DIM).getPitches()));
        //                LOG.debug("DIM7 degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.DIM7).getPitches()));
        //                LOG.debug("DOM7 degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.DOM7).getPitches()));
        //                LOG.debug("MAJ7 degree: {}, chord: {}", j+1, Arrays.asList(Chord.getChord(pitches[j], ChordType.MAJ7).getPitches()));
        //                LOG.debug("############################################");
        //            }
        //        }
        //

        for(int i = 0; i < noteNames.length; i++) {

            Pitch pitch = new Pitch(i);

            LOG.debug("*************************************************************");
            LOG.debug("scale: {}, pitch: {}", noteNames[i], pitch);


            for(int k = 0; k < ChordDegree.values().length; k++) {
                LOG.debug("degree: {}, chord: {}", k+1, Arrays.asList(Chord.getChordDegree(pitch, ChordType.MIN, ChordDegree.values()[k]).getPitches()));
            }
            LOG.debug("############################################");

        }

    }


}
