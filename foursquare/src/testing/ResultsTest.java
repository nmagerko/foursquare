package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.foyt.foursquare.api.entities.CompactVenue;
import flexjson.JSONDeserializer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import client.FoursquareResult;

public class ResultsTest {
	// the number of results in the input file
	private static final int EXPECTED_NUMBER_OF_RESULTS = 15;
	// the deserializer that will be used for getting the CompactVenues from the input
	private static JSONDeserializer<List<CompactVenue>> deserializer;
	private static File testResults;
	// an array of CompactVenues, as required for instantiating a FoursquareResult
	private static CompactVenue[] rawVenueResults;
	private FoursquareResult results;

	/**
	 * Sets up the unit test by getting the data from an already-
	 * executed search, and storing it in a CompactVenue array
	 * @throws Exception
	 */
	@BeforeClass
	public static void initialize() throws Exception {
		deserializer = new JSONDeserializer<>();
		// grab the results that will be used in the unit test
		testResults = new File("src/testing/RawVenueResults.json");
		// generate a List of CompactVenues from the data stored in the file
		List<CompactVenue> venueList = deserializer.deserialize((Reader)new FileReader(testResults));
		// initialize raw results array
		rawVenueResults = new CompactVenue[venueList.size()];
		// add the venues into this array
		for (int index = 0; index < rawVenueResults.length; index++){
			rawVenueResults[index] = venueList.get(index);
		}
	}
	
	/**
	 * Sets up each unit test by recreating the results from the
	 * data that is loaded from initialization()
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create the FoursquareResult from the data
		results = new FoursquareResult(rawVenueResults);
		// check that it was initialized properly
		assertNotNull("FoursquareResults object initialized", results);
	}

	/**
	 * Tests to be sure that the FoursquareResult contains the correct
	 * number of results
	 */
	@Test
	public void resultAmountTest(){
		List<String> resultsList = results.getResults();
		// check to make sure the expected amount of results were stored
		assertTrue("Correct number of results", resultsList.size() == EXPECTED_NUMBER_OF_RESULTS);
	}
	
	/**
	 * Tests to be sure that the proper Foursquare results are contained
	 * in the FoursquareResult instance, and that there are no duplicates
	 * in the results
	 */
	@Test
	public void fidelityTest() {
		List<String> resultsList = results.getResults();
		// check to make sure that the unique Foursquare ID field is in both the input 
		// and the resultsList. Keep track of the indices that are tested, to make sure 
		// that all are tested (no duplicates, other issues)
		Set<Integer> resultsTested = new HashSet<>();
		boolean businessMatchFound = false;
		for (CompactVenue business : rawVenueResults){
			for (int index = 0; index < resultsList.size(); index++){
				if (resultsList.get(index).contains(business.getId())){
					businessMatchFound = true;
					resultsTested.add(index);
					break;
				}
			}
			// check to make sure that a business was found in the results, or fail
			assertTrue("The business in the input was found in the results", businessMatchFound);
			// reset the "match found" boolean
			businessMatchFound = false;
		}
		// check to make sure that all results were tested
		assertTrue("All results in the FoursquareResult were found in the input file", 
						resultsList.size() == resultsTested.size());
	}

}