/*
 * Copyright 2010, Silvio Heuberger @ IFS www.ifs.hsr.ch
 *
 * This code is release under the LGPL license.
 * You should have received a copy of the license
 * in the LICENSE file. If you have not, see
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package ch.hsr.geohash;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.geohash.util.GeoHashSizeTable;

public class GeoHashBoundingBoxSearch {

	private BoundingBox boundingBox;
	private List<GeoHash> searchHashes = new ArrayList<GeoHash>(9);

	/**
	 * return the hash(es) that approximate this bounding box.
	 */
	public GeoHashBoundingBoxSearch(BoundingBox bbox) {
		int fittingBits = GeoHashSizeTable.numberOfBitsForOverlappingGeoHash(bbox);
		WGS84Point center = bbox.getCenterPoint();
		GeoHash centerHash = GeoHash.withBitPrecision(center.getLatitude(), center.getLongitude(), fittingBits);
		boundingBox = centerHash.getBoundingBox();

		if (hashFits(centerHash, bbox)) {
			System.out.println("yay, centered hash fits.");
			addSearchHash(centerHash);
		} else {
			expandSearch(centerHash, bbox);
		}
	}

	private void expandSearch(GeoHash centerHash, BoundingBox bbox) {
		assert centerHash.getBoundingBox().intersects(bbox) : "center hash must at least intersect the bounding box!";
		addSearchHash(centerHash);

		for (GeoHash adjacent : centerHash.getAdjacent()) {
			if (adjacent.getBoundingBox().intersects(bbox) && !searchHashes.contains(adjacent)) {
				addSearchHash(adjacent);
			}
		}
	}

	private void addSearchHash(GeoHash hash) {
		searchHashes.add(hash);
		expandSearchBoundingBox(hash);
	}

	private void expandSearchBoundingBox(GeoHash hash) {
		// TODO: adjust the bounding box size with the added hashes.
	}

	private boolean hashFits(GeoHash hash, BoundingBox bbox) {
		return hash.contains(bbox.getUpperLeft()) && hash.contains(bbox.getLowerRight());
	}

	public List<GeoHash> getSearchHashes() {
		return searchHashes;
	}
}
