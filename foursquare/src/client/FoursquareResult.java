package client;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.foursquare.api.entities.CompactVenue;
import flexjson.JSONSerializer;

/**
 * Encapsulates the data returned by a Foursquare query,
 * storing the data in a private structure that is accessible only
 * through this class
 * @author Nick Magerko
 *
 */
public class FoursquareResult {
	// a List of businesses that were returned by the venue search
	private List<Business> businesses;
	
	/**
	 * TODO: General comment
	 * @author Nick Magerko
	 *
	 */
	@SuppressWarnings("unused")
	private static class Business {
		private static final JSONSerializer serializer = new JSONSerializer();
		// fields based on what I see on Whittl's site, as well as
		// the business's ID is added for reference purposes
		// note that the API does not allow access to business hours...
		private String foursquareID;
		private String bestKnownName;
		private String phoneNumber;
		private String contactEmail;
		private String twitterAccount;
		private String facebookPage;
		private String website;
		private String formattedAddress;
		private String[] associatedCategories;
		private double latitude;
		private double longitude;
		private boolean informationVerified;
		
		public Business(String ID, String name, String phoneNumber, String email, String twitter,
						String facebook, String website, String address, String[] categories,
						double latitude, double longitude, boolean informationVerified){
			this.foursquareID = ID;
			this.bestKnownName = name;
			this.phoneNumber = phoneNumber;
			this.contactEmail = email;
			this.twitterAccount = twitter;
			this.facebookPage = facebook;
			this.website = website;
			this.formattedAddress = address;
			this.associatedCategories = categories;
			this.latitude = latitude;
			this.longitude = longitude;
			this.informationVerified = informationVerified;
		}
		
		// accessor methods utilized by flexJSON for serialization
		public String getFoursquareID() { return foursquareID; }
		public String getBestKnownName() { return bestKnownName; }
		public String getPhoneNumber() { return phoneNumber; }
		public String getContactEmail() { return contactEmail; }
		public String getTwitterAccount() { return twitterAccount; }
		public String getFacebookPage() { return facebookPage; }
		public String getWebsite() { return website; }
		public String getFormattedAddress() { return formattedAddress; }
		public String[] getAssociatedCategories() { return associatedCategories; }
		public double getLatitude() { return latitude; }
		public double getLongitude() { return longitude; }
		public boolean isInformationVerified() { return informationVerified; }

		/*public void setFoursquareID(String foursquareID) { this.foursquareID = foursquareID; }
		public void setBestKnownName(String bestKnownName) { this.bestKnownName = bestKnownName; }
		public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
		public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
		public void setTwitterAccount(String twitterAccount) { this.twitterAccount = twitterAccount; }
		public void setFacebookPage(String facebookPage) { this.facebookPage = facebookPage; }
		public void setWebsite(String website) { this.website = website; }
		public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }
		public void setAssociatedCategories(String[] associatedCategories) { this.associatedCategories = associatedCategories; }
		public void setLatitude(double latitude) { this.latitude = latitude; }
		public void setLongitude(double longitude) { this.longitude = longitude; }*/
		
		public String toJSON(){
			return serializer.exclude("class").deepSerialize(this);
		}
	}

	public FoursquareResult(CompactVenue[] searchResults){
		businesses = new ArrayList<>();
		for (CompactVenue venue : searchResults){
			String venueID = venue.getId();
			String venueName = venue.getName();
			String venuePhoneNumber = venue.getContact().getPhone();
			String venueEmail = venue.getContact().getEmail();
			String venueTwitter = venue.getContact().getTwitter();
			String venueFacebook = venue.getContact().getFacebook();
			String venueWebsite = venue.getUrl();
			String venueAddress;
			try {
				venueAddress = venue.getLocation().getAddress() + ", " +
								 	  venue.getLocation().getCity() + ", " + 
								 	  venue.getLocation().getState() + " " + 
								 	  venue.getLocation().getPostalCode();
			}
			catch (NullPointerException exception){
				venueAddress = null;
			}
			String[] venueCategories = new String[venue.getCategories().length];
			for (int index = 0; index < venue.getCategories().length; index++){
				venueCategories[index] = venue.getCategories()[index].getName();
			}
			double latitude = (double)venue.getLocation().getLat();
			double longitude = (double)venue.getLocation().getLng();
			boolean venueInfoVerified = venue.getVerified();
			
			businesses.add(new Business(venueID, venueName, venuePhoneNumber, venueEmail, venueTwitter, venueFacebook, venueWebsite,
						  				venueAddress, venueCategories, latitude, longitude, venueInfoVerified));
		}
	}

	public List<String> getResults(){
		List<String> results = new ArrayList<>();
		for (Business business : businesses){
			results.add(business.toJSON());
		}
		return results;
	}

}