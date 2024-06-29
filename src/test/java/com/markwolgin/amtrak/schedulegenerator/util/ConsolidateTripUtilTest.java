package com.markwolgin.amtrak.schedulegenerator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedResponseObject;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ConsolidateTripUtilTest {

    @InjectMocks
    private ConsolidateTripUtil consolidateTripUtil;

    private final ClassLoader classLoader = TimetableUtilTest.class.getClassLoader();
    private final ConsolidatedResponseObject consolidatedResponseObject = loadData(classLoader.getResourceAsStream("data_response.json"));

    private ConsolidatedResponseObject loadData(InputStream resourceAsStream) {
        try {
            return new ObjectsUtil(new ObjectMapper().registerModule(new JavaTimeModule())).loadObject(resourceAsStream, ConsolidatedResponseObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void aggregateSimilarConsolidatedTrips() {
    }

    @Nested
    @DisplayName("Create Super Consolidated Trip")
    class CreateSuperConsolidatedTrip {

        @Test
        void createSuperConsolidatedTrip() {
            List<ConsolidatedTrip> consolidatedTrips = new ArrayList<>(consolidatedResponseObject.getRequestedConsolidatedRoutes()
                    .get().get("94").getTripList().get().values());
            Map<Integer, List<ConsolidatedTrip>> result = consolidateTripUtil.buildTripsListByHash(consolidatedTrips);

            List<ConsolidatedTrip> evenShorterList;
            for (List<ConsolidatedTrip> shorterList: result.values()) {
                evenShorterList = consolidateTripUtil.createSuperConsolidatedTrip(shorterList);
                Assertions.assertTrue(shorterList.size() >= evenShorterList.size());
            }
        }
    }

    @Nested
    @DisplayName("Build Trips List By Hash")
    class BuildTripsListByHash {

        @Test
        void buildTripsListByHash() {
            List<ConsolidatedTrip> consolidatedTrips = new ArrayList<>(consolidatedResponseObject.getRequestedConsolidatedRoutes()
                    .get().get("94").getTripList().get().values());
            Map<Integer, List<ConsolidatedTrip>> result = consolidateTripUtil.buildTripsListByHash(consolidatedTrips);

            log.info("Breaking...");
            Assertions.assertEquals(179, result.size());
            Assertions.assertEquals(287, consolidatedTrips.size());
        }
    }

    @Nested
    @DisplayName("Calculated Custom Hash Code")
    public class CalculatedCustomHashCode {

        @Test
        void calculateCustomHashCode() {
            List<ConsolidatedTrip> consolidatedTripCollection
                    = new ArrayList<>(consolidatedResponseObject.getRequestedConsolidatedRoutes()
                    .get().get("94").getTripList().get().values());

            Map<Integer, LinkedList<ConsolidatedTrip>> hashCodeCheck = new HashMap<>();
            LinkedList<ConsolidatedTrip> consolidatedTrips;
            int a;
            for (ConsolidatedTrip consolidatedTrip: consolidatedTripCollection) {
                a = consolidateTripUtil.calculateCustomHashCode(consolidatedTrip);
                consolidatedTrips = hashCodeCheck.get(a);
                if (consolidatedTrips == null)  {
                    consolidatedTrips = new LinkedList<>();
                    hashCodeCheck.put(a, consolidatedTrips);
                }
                consolidatedTrips.add(consolidatedTrip);
            }
            log.info("Breaking...");
            Assertions.assertEquals(179, hashCodeCheck.size());
            Assertions.assertEquals(287, consolidatedTripCollection.size());

            Assertions.assertDoesNotThrow(() -> {
                ConsolidatedTrip reference;
                for (Map.Entry<Integer, LinkedList<ConsolidatedTrip>> entry: hashCodeCheck.entrySet()) {
                    reference = entry.getValue().get(0);
                    for (ConsolidatedTrip consolidatedTrip: entry.getValue()) {
                        for (int k = 0; k < reference.getTripStops().size(); k ++) {
                            reference.getTripStops().get(k).setTripId("STRIPPED_FOR_TEST");
                            consolidatedTrip.getTripStops().get(k).setTripId("STRIPPED_FOR_TEST");
                            Assertions.assertEquals(reference.getTripStops().get(k), consolidatedTrip.getTripStops().get(k),
                                    "Stops not identical.");
                        }
                    }
                }
                log.info("Checked all the build lists, hash code looks to be working.");
            });
        }
    }


    @Nested
    @DisplayName("Reduced Operating Pattern")
    class ReduceOpertatingPattern {

        private List<ConsolidatedTrip> consolidatedTripList;
        private final ConsolidatedTrip a = new ConsolidatedTrip();
        private final ConsolidatedTrip b = new ConsolidatedTrip();
        private final ConsolidatedTrip c = new ConsolidatedTrip();
        private final ConsolidatedTrip d = new ConsolidatedTrip();
        private final ConsolidatedTrip e = new ConsolidatedTrip();

        @BeforeEach
        void setUp() {
            a.setOperatingPattern(new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(false).saturday(false).sunday(false));
            b.setOperatingPattern(new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(true).saturday(false).sunday(false));
            c.setOperatingPattern(new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(false).saturday(true).sunday(true));
            d.setOperatingPattern(new OperatingPattern().monday(true).tuesday(false).wednesday(true).thursday(false).friday(false).saturday(true).sunday(false));
            e.setOperatingPattern(new OperatingPattern().monday(false).tuesday(false).wednesday(true).thursday(false).friday(false).saturday(false).sunday(false));
        }


        @Test
        void reducedOperatingPatternTest_AB() {
            this.consolidatedTripList = List.of(a, b);
            OperatingPattern result = consolidateTripUtil.reduceOpertatingPattern(this.consolidatedTripList);

            Assertions.assertNotEquals(result, a.getOperatingPattern());
            Assertions.assertNotEquals(result, b.getOperatingPattern());
            Assertions.assertEquals(result, new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(false).sunday(false));
        }

        @Test
        void reducedOperatingPatternTest_AE() {
            this.consolidatedTripList = List.of(a, e);
            OperatingPattern result = consolidateTripUtil.reduceOpertatingPattern(this.consolidatedTripList);

            Assertions.assertEquals(result, a.getOperatingPattern());
            Assertions.assertNotEquals(result, e.getOperatingPattern());
            Assertions.assertEquals(result, new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(false).saturday(false).sunday(false));
        }

        @Test
        void reducedOperatingPatternTest_ABCDE() {
            this.consolidatedTripList = List.of(a, b, c, d, e);
            OperatingPattern result = consolidateTripUtil.reduceOpertatingPattern(this.consolidatedTripList);

            Assertions.assertNotEquals(result, a.getOperatingPattern());
            Assertions.assertNotEquals(result, b.getOperatingPattern());
            Assertions.assertNotEquals(result, c.getOperatingPattern());
            Assertions.assertNotEquals(result, d.getOperatingPattern());
            Assertions.assertNotEquals(result, e.getOperatingPattern());
            Assertions.assertEquals(result, new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(true).sunday(true));
        }
    }


}