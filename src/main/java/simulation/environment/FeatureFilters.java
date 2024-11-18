package simulation.environment;

import java.util.ArrayList;
import java.util.List;

import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.factory.CommonFactoryFinder;

public class FeatureFilters {

    private FilterFactory F;

    private Filter residentialFilter;
    private Filter schoolFilter;
    private Filter universityFilter;
    private Filter hospitalFilter;
    private Filter essentialAmenitiesFilter;
    private Filter essentialWorkplacesFilter;
    private Filter nonEssentialAmenitiesFilter;
    private Filter nonEssentialWorkplacesFilter;

    public Filter getResidentialFilter() {
        return residentialFilter;
    }

    public Filter getSchoolFilter() {
        return schoolFilter;
    }

    public Filter getUniversityFilter() {
        return universityFilter;
    }

    public Filter getHospitalFilter() {
        return hospitalFilter;
    }

    public Filter getEssentialAmenitiesFilter() {
        return essentialAmenitiesFilter;
    }

    public Filter getEssentialWorkplacesFilter() {
        return essentialWorkplacesFilter;
    }

    public Filter getNonEssentialAmenitiesFilter() {
        return nonEssentialAmenitiesFilter;
    }

    public Filter getNonEssentialWorkplacesFilter() {
        return nonEssentialWorkplacesFilter;
    }

    public FeatureFilters() {
        F = CommonFactoryFinder.getFilterFactory();
        residentialFilter = isResidential();
        schoolFilter = isSchool();
        universityFilter = isUniversity();
        hospitalFilter = isHospital();
        essentialAmenitiesFilter = isEssentialAmenity();
        essentialWorkplacesFilter = isEssentialWorkplace();
        nonEssentialAmenitiesFilter = isNonEssentialAmenity();
        nonEssentialWorkplacesFilter = isNonEssentialWorkplace();
    }

    private Filter isBuilding() {
        return F.notEqual(F.property("building"), F.literal(""));
    }

    private Filter isResidential() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("residential")));
        filters.add(F.equals(F.property("building"), F.literal("house")));
        filters.add(F.equals(F.property("building"), F.literal("apartments")));
        filters.add(F.equals(F.property("building"), F.literal("detached")));
        filters.add(F.equals(F.property("building"), F.literal("terrace")));
        filters.add(F.equals(F.property("building"), F.literal("appartments")));
        filters.add(F.equals(F.property("building"), F.literal("barracks")));
        filters.add(F.equals(F.property("building"), F.literal("bungalow")));
        filters.add(F.equals(F.property("building"), F.literal("cabin")));
        filters.add(F.equals(F.property("building"), F.literal("detached")));
        filters.add(F.equals(F.property("building"), F.literal("annexe")));
        filters.add(F.equals(F.property("building"), F.literal("dormitory")));
        filters.add(F.equals(F.property("building"), F.literal("farm")));
        filters.add(F.equals(F.property("building"), F.literal("ger")));
        filters.add(F.equals(F.property("building"), F.literal("hotel")));
        filters.add(F.equals(F.property("building"), F.literal("house")));
        filters.add(F.equals(F.property("building"), F.literal("houseboat")));
        filters.add(F.equals(F.property("building"), F.literal("residential")));
        filters.add(F.equals(F.property("building"), F.literal("semidetached_house")));
        filters.add(F.equals(F.property("building"), F.literal("static_caravan")));
        filters.add(F.equals(F.property("building"), F.literal("stilt_house")));
        filters.add(F.equals(F.property("building"), F.literal("terrace")));
        filters.add(F.equals(F.property("building"), F.literal("tree_house")));
        filters.add(F.equals(F.property("building"), F.literal("trullo")));
        filters.add(F.equals(F.property("building"), F.literal("garage")));
        filters.add(F.equals(F.property("building"), F.literal("garages")));

        return F.or(filters);
    }

    private Filter isSchool() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("school")));
        filters.add(F.equals(F.property("building"), F.literal("kindergarten")));
        filters.add(F.equals(F.property("building"), F.literal("college")));
        filters.add(F.equals(F.property("amenity"), F.literal("school")));
        filters.add(F.equals(F.property("amenity"), F.literal("kindergarten")));
        filters.add(F.equals(F.property("amenity"), F.literal("college")));

        return F.and(isBuilding(), F.or(filters));
    }

    private Filter isUniversity() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("university")));
        filters.add(F.equals(F.property("amenity"), F.literal("university")));

        return F.and(isBuilding(), F.or(filters));
    }

    private Filter isHospital() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("hospital")));
        filters.add(F.equals(F.property("amenity"), F.literal("hospital")));
        filters.add(F.equals(F.property("amenity"), F.literal("clinic")));

        return F.and(isBuilding(), F.or(filters));
    }

    private Filter isEssentialAmenity() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("parking")));
        filters.add(F.equals(F.property("building"), F.literal("supermarket")));
        filters.add(F.equals(F.property("building"), F.literal("public")));
        filters.add(F.equals(F.property("building"), F.literal("train_station")));
        filters.add(F.equals(F.property("building"), F.literal("transportation")));
        filters.add(F.equals(F.property("amenity"), F.literal("bus_station")));
        filters.add(F.equals(F.property("amenity"), F.literal("car_rental")));
        filters.add(F.equals(F.property("amenity"), F.literal("fuel")));
        filters.add(F.equals(F.property("amenity"), F.literal("bank")));
        filters.add(F.equals(F.property("amenity"), F.literal("bureau_de_change")));
        filters.add(F.equals(F.property("amenity"), F.literal("money_transfer")));
        filters.add(F.equals(F.property("amenity"), F.literal("payment_centre")));
        filters.add(F.equals(F.property("amenity"), F.literal("dentist")));
        filters.add(F.equals(F.property("amenity"), F.literal("doctors")));
        filters.add(F.equals(F.property("amenity"), F.literal("nursing_home")));
        filters.add(F.equals(F.property("amenity"), F.literal("pharmacy")));
        filters.add(F.equals(F.property("amenity"), F.literal("social_facility")));
        filters.add(F.equals(F.property("amenity"), F.literal("veterinary")));
        filters.add(F.equals(F.property("amenity"), F.literal("courthouse")));
        filters.add(F.equals(F.property("amenity"), F.literal("post_office")));
        filters.add(F.equals(F.property("amenity"), F.literal("townhall")));
        filters.add(F.equals(F.property("shop"), F.literal("bakery")));
        filters.add(F.equals(F.property("shop"), F.literal("butcher")));
        filters.add(F.equals(F.property("shop"), F.literal("convenience")));
        filters.add(F.equals(F.property("shop"), F.literal("dairy")));
        filters.add(F.equals(F.property("shop"), F.literal("deli")));
        filters.add(F.equals(F.property("shop"), F.literal("farm")));
        filters.add(F.equals(F.property("shop"), F.literal("food")));
        filters.add(F.equals(F.property("shop"), F.literal("frozen_food")));
        filters.add(F.equals(F.property("shop"), F.literal("greengrocer")));
        filters.add(F.equals(F.property("shop"), F.literal("health_food")));
        filters.add(F.equals(F.property("shop"), F.literal("water")));
        filters.add(F.equals(F.property("shop"), F.literal("wholesale")));
        filters.add(F.equals(F.property("shop"), F.literal("supermarket")));
        filters.add(F.equals(F.property("shop"), F.literal("chemist")));
        filters.add(F.equals(F.property("shop"), F.literal("medical_supply")));
        filters.add(F.equals(F.property("shop"), F.literal("optician")));
        filters.add(F.equals(F.property("shop"), F.literal("agrarian")));
        filters.add(F.equals(F.property("shop"), F.literal("hardware")));
        filters.add(F.equals(F.property("shop"), F.literal("car")));
        filters.add(F.equals(F.property("shop"), F.literal("car_parts")));
        filters.add(F.equals(F.property("shop"), F.literal("car_repair")));
        filters.add(F.equals(F.property("shop"), F.literal("dry_cleaning")));
        filters.add(F.equals(F.property("shop"), F.literal("funeral_directors")));
        filters.add(F.equals(F.property("shop"), F.literal("laundry")));
        filters.add(F.equals(F.property("shop"), F.literal("pet")));
        filters.add(F.equals(F.property("shop"), F.literal("storage_rental")));

        return F.and(isBuilding(), F.or(filters));
    }

    private Filter isEssentialWorkplace() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.equals(F.property("building"), F.literal("bridge")));
        filters.add(F.equals(F.property("building"), F.literal("fire_station")));
        filters.add(F.equals(F.property("building"), F.literal("government")));
        filters.add(F.equals(F.property("building"), F.literal("gatehouse")));
        filters.add(F.equals(F.property("building"), F.literal("barn")));
        filters.add(F.equals(F.property("building"), F.literal("cowshed")));
        filters.add(F.equals(F.property("building"), F.literal("farm_auxiliary")));
        filters.add(F.equals(F.property("building"), F.literal("greenhouse")));
        filters.add(F.equals(F.property("building"), F.literal("slurry_tank")));
        filters.add(F.equals(F.property("building"), F.literal("stable")));
        filters.add(F.equals(F.property("building"), F.literal("sty")));
        filters.add(F.equals(F.property("building"), F.literal("livestock")));
        filters.add(F.equals(F.property("building"), F.literal("digester")));
        filters.add(F.equals(F.property("building"), F.literal("service")));
        filters.add(F.equals(F.property("building"), F.literal("tech_cab")));
        filters.add(F.equals(F.property("building"), F.literal("transformer_tower")));
        filters.add(F.equals(F.property("building"), F.literal("water_tower")));
        filters.add(F.equals(F.property("building"), F.literal("storage_tank")));
        filters.add(F.equals(F.property("building"), F.literal("silo")));
        filters.add(F.equals(F.property("amenity"), F.literal("fire_station")));
        filters.add(F.equals(F.property("amenity"), F.literal("police")));
        filters.add(F.equals(F.property("amenity"), F.literal("post_depot")));
        filters.add(F.equals(F.property("amenity"), F.literal("prison")));
        filters.add(F.equals(F.property("amenity"), F.literal("mailroom")));

        return F.and(isBuilding(), F.or(filters));
    }

    private Filter isNonEssential() {
        List<Filter> filters = new ArrayList<>();
        filters.add(F.not(isResidential()));
        filters.add(F.not(isSchool()));
        filters.add(F.not(isUniversity()));
        filters.add(F.not(isHospital()));
        filters.add(F.not(isEssentialAmenity()));
        filters.add(F.not(isEssentialWorkplace()));

        return F.and(isBuilding(), F.and(filters));
    }

    private Filter isNonEssentialAmenity() {
        List<Filter> filters = new ArrayList<>();

        filters.add(F.equals(F.property("building"), F.literal("kiosk")));
        filters.add(F.equals(F.property("building"), F.literal("retail")));
        filters.add(F.equals(F.property("building"), F.literal("religious")));
        filters.add(F.equals(F.property("building"), F.literal("cathedral")));
        filters.add(F.equals(F.property("building"), F.literal("chapel")));
        filters.add(F.equals(F.property("building"), F.literal("church")));
        filters.add(F.equals(F.property("building"), F.literal("kingdom_hall")));
        filters.add(F.equals(F.property("building"), F.literal("monastery")));
        filters.add(F.equals(F.property("building"), F.literal("mosque")));
        filters.add(F.equals(F.property("building"), F.literal("presbytery")));
        filters.add(F.equals(F.property("building"), F.literal("shrine")));
        filters.add(F.equals(F.property("building"), F.literal("synagogue")));
        filters.add(F.equals(F.property("building"), F.literal("temple")));
        filters.add(F.equals(F.property("building"), F.literal("bakehouse")));
        filters.add(F.equals(F.property("building"), F.literal("civic")));
        filters.add(F.equals(F.property("building"), F.literal("museum")));
        filters.add(F.equals(F.property("building"), F.literal("grandstand")));
        filters.add(F.equals(F.property("building"), F.literal("pavilion")));
        filters.add(F.equals(F.property("building"), F.literal("riding_hall")));
        filters.add(F.equals(F.property("building"), F.literal("sports_hall")));
        filters.add(F.equals(F.property("building"), F.literal("sports_centre")));
        filters.add(F.equals(F.property("building"), F.literal("stadium")));
        filters.add(F.equals(F.property("building"), F.literal("allotment_house")));
        filters.add(F.equals(F.property("building"), F.literal("boathouse")));
        filters.add(F.equals(F.property("building"), F.literal("hangar")));
        filters.add(F.equals(F.property("building"), F.literal("beach_hut")));
        filters.add(F.equals(F.property("building"), F.literal("castle")));
        filters.add(F.equals(F.property("building"), F.literal("pagoda")));
        filters.add(F.equals(F.property("building"), F.literal("tower")));

        filters.add(F.notEqual(F.property("amenity"), F.literal("")));
        filters.add(F.notEqual(F.property("shop"), F.literal("")));
        filters.add(F.notEqual(F.property("leisure"), F.literal("")));
        filters.add(F.notEqual(F.property("tourism"), F.literal("")));

        return F.and(isNonEssential(), F.or(filters));
    }

    private Filter isNonEssentialWorkplace() {
        return F.and(isNonEssential(), F.not(isNonEssentialAmenity()));
    }
}
