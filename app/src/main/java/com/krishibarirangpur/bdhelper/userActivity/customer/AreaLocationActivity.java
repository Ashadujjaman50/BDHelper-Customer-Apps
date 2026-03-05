package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.AreaAdapter;
import com.krishibarirangpur.bdhelper.adapter.customer.CityAdapter;
import com.krishibarirangpur.bdhelper.adapter.customer.SubAreaAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityAreaLocationBinding;
import com.krishibarirangpur.bdhelper.model.Area;
import com.krishibarirangpur.bdhelper.model.City;
import com.krishibarirangpur.bdhelper.model.SubArea;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class AreaLocationActivity extends BaseActivity {

    private ActivityAreaLocationBinding binding;

    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;
    private SubAreaAdapter subAreaAdapter;
    private List<City> cityList;
    private List<Area> areaList;
    private List<SubArea> subAreaList;
    private String selectedCityId, selectedCityName, selectedAreaId, selectedAreaName, selectedSubAreaName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_area_location);

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //load All List
        fillCityList();
        fillAreaList();
        fillSubAreaList();

        setUpRecyclerView();


        cityAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                City currentCity = cityList.get(position);
                selectedCityId = currentCity.getCityId();
                selectedCityName = currentCity.getCityName();

                binding.cityNameTV.setVisibility(View.VISIBLE);
                binding.cityNameTV.setText(selectedCityName);

                binding.cityRecyclerView.setVisibility(View.GONE);
                binding.areaRecyclerView.setVisibility(View.VISIBLE);


                areaAdapter.getFilter().filter(selectedCityId);

            }


            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });

        areaAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Area currentArea = areaList.get(position);
                selectedAreaId = currentArea.getAreaId();
                selectedAreaName = currentArea.getAreaName();

                binding.areaNameTV.setVisibility(View.VISIBLE);
                binding.areaNameTV.setText(selectedAreaName);

                binding.areaRecyclerView.setVisibility(View.GONE);
                binding.subAreaRecyclerView.setVisibility(View.VISIBLE);

                subAreaAdapter.getFilter().filter(selectedAreaId);

            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });

        subAreaAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SubArea currentSubArea = subAreaList.get(position);
                //selectedSubAreaId = currentSubArea.getSubAreaId();
                selectedSubAreaName = currentSubArea.getSubAreaName();

                binding.subAreaNameTV.setText(selectedSubAreaName);

                binding.subAreaRecyclerView.setVisibility(View.GONE);
                binding.lastAreaRecyclerView.setVisibility(View.VISIBLE);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", selectedSubAreaName + ", " + selectedAreaName + ", " + selectedCityName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });


    }

    private void setUpRecyclerView() {
        binding.cityRecyclerView.setHasFixedSize(true);
        cityAdapter = new CityAdapter(cityList);
        binding.cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cityRecyclerView.setAdapter(cityAdapter);

        binding.areaRecyclerView.setHasFixedSize(true);
        areaAdapter = new AreaAdapter(areaList);
        binding.areaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.areaRecyclerView.setAdapter(areaAdapter);

        binding.subAreaRecyclerView.setHasFixedSize(true);
        subAreaAdapter = new SubAreaAdapter(subAreaList);
        binding.subAreaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.subAreaRecyclerView.setAdapter(subAreaAdapter);
    }


    private void fillCityList() {
        cityList = new ArrayList<>();
        cityList.add(new City("1", getString(R.string.dhaka_city)));
        cityList.add(new City("2", getString(R.string.chittagong_city)));
        cityList.add(new City("Dhaka", getString(R.string.dhaka_division)));
        cityList.add(new City("Barisal", getString(R.string.barisal_division)));
        cityList.add(new City("Chittagong", getString(R.string.chittagong_division)));
        cityList.add(new City("Mymensingh", getString(R.string.mymensingh_division)));
        cityList.add(new City("Khulna", getString(R.string.khulna_division)));
        cityList.add(new City("Rajshahi", getString(R.string.rajshahi_division)));
        cityList.add(new City("Rangpur", getString(R.string.rangpur_division)));
        cityList.add(new City("Sylhet", getString(R.string.sylhet_division)));

    }

    private void fillAreaList() {
        areaList = new ArrayList<>();
        areaList.add(new Area("1", "Uttara", getString(R.string.uttara)));
        areaList.add(new Area("1", "Kafrul", getString(R.string.kafrul)));
        areaList.add(new Area("1", "KamrangirChor", getString(R.string.kamrangir_char)));
        areaList.add(new Area("1", "Kotoyaly", getString(R.string.kotoyali)));
        areaList.add(new Area("1", "Cantonment", getString(R.string.cantonment)));
        areaList.add(new Area("1", "Khilgaon", getString(R.string.khilgaon)));
        areaList.add(new Area("1", "Gulshan", getString(R.string.gulsan)));
        areaList.add(new Area("1", "Demra", getString(R.string.demra)));
        areaList.add(new Area("1", "Tejgaon", getString(R.string.tejgaon)));
        areaList.add(new Area("1", "Dhanmondi", getString(R.string.dhanmondi)));
        areaList.add(new Area("1", "Pollobi", getString(R.string.pollobi)));
        areaList.add(new Area("1", "Bosundhara", getString(R.string.bosundhara)));
        areaList.add(new Area("1", "Badda", getString(R.string.badda)));
        areaList.add(new Area("1", "Motijhil", getString(R.string.motijhil)));
        areaList.add(new Area("1", "Mirpur", getString(R.string.mirpur)));
        areaList.add(new Area("1", "Mohammadpur", getString(R.string.mohammadpur)));
        areaList.add(new Area("1", "Ramona", getString(R.string.ramona)));
        areaList.add(new Area("1", "Lalbag", getString(R.string.lalbug)));
        areaList.add(new Area("1", "Shampur", getString(R.string.shampur)));
        areaList.add(new Area("1", "ShobujBug", getString(R.string.sobuj_bug)));
        areaList.add(new Area("1", "Sutrapur", getString(R.string.sutrapur)));
        areaList.add(new Area("1", "Hajaribug", getString(R.string.hajaribug)));


        areaList.add(new Area("2", "Akborshah", getString(R.string.akbor_shah)));
        areaList.add(new Area("2", "Andharkilla", getString(R.string.andhar_killa)));
        areaList.add(new Area("2", "AmirbugAbashikalaka", getString(R.string.amirbug_abashik_alaka)));
        areaList.add(new Area("2", "Alishapara", getString(R.string.ali_shapara)));
        areaList.add(new Area("2", "Asadgonjcommercialarea", getString(R.string.asadganj_commercial_area)));
        areaList.add(new Area("2", "EPZ", getString(R.string.epz)));
        areaList.add(new Area("2", "Uttarpathantuli", getString(R.string.uttar_pathantuli)));
        areaList.add(new Area("2", "Anayetbazar", getString(R.string.anayet_bazar)));
        areaList.add(new Area("2", "KarnafuliResidentialarea", getString(R.string.karnafuli_residential_area)));
        areaList.add(new Area("2", "Kotoyali", getString(R.string.kotoyali2)));
        areaList.add(new Area("2", "Khatungonj", getString(R.string.khatungonj)));
        areaList.add(new Area("2", "Khulshi", getString(R.string.khulshi)));
        areaList.add(new Area("2", "Gosaildanga", getString(R.string.gosaildanga)));
        areaList.add(new Area("2", "Chowkbazar", getString(R.string.chowk_bazar)));
        areaList.add(new Area("2", "Chattagrambondor", getString(R.string.chattagram_bondor)));
        areaList.add(new Area("2", "Chorpara", getString(R.string.chor_para)));

        areaList.add(new Area("2", "Chorhalda", getString(R.string.chor_halda)));
        areaList.add(new Area("2", "Chadgao", getString(R.string.chadgao)));
        areaList.add(new Area("2", "ChowdhuryPara", getString(R.string.chowdhury_para)));
        areaList.add(new Area("2", "GEMofficerscolony", getString(R.string.gem_officers_colony)));
        areaList.add(new Area("2", "Jhawtola", getString(R.string.jhawtola)));
        areaList.add(new Area("2", "Tigerpassrailwaycolony", getString(R.string.tigerpass_railway_colony)));
        areaList.add(new Area("2", "TSPcolony", getString(R.string.tsp_colony)));
        areaList.add(new Area("2", "Teribazar", getString(R.string.teri_bazar)));
        areaList.add(new Area("2", "Doublemuring", getString(R.string.double_muring)));
        areaList.add(new Area("2", "Doijpara", getString(R.string.doijpara)));
        areaList.add(new Area("2", "Southpatenga", getString(R.string.south_patenga)));
        areaList.add(new Area("2", "Southbondar", getString(R.string.south_bondar)));
        areaList.add(new Area("2", "Dokkhin moddho holyshohor", getString(R.string.dokkhin_moddho_holyshohor)));
        areaList.add(new Area("2", "Dampara", getString(R.string.dampara)));
        areaList.add(new Area("2", "Deoyanghat", getString(R.string.deoyan_ghat)));
        areaList.add(new Area("2", "Dewanbazar", getString(R.string.deoyan_bazar)));
        areaList.add(new Area("2", "Dhumpara", getString(R.string.dhumpara)));
        areaList.add(new Area("2", "Northmiddleholishohor", getString(R.string.north_middle_holishohor)));
        areaList.add(new Area("2", "Nasirabad", getString(R.string.nasirabadh)));
        areaList.add(new Area("2", "Newmuring", getString(R.string.new_muring)));
        areaList.add(new Area("2", "Navyport", getString(R.string.navy_port)));
        areaList.add(new Area("2", "Podmaabashikalaka", getString(R.string.podma_abashik_alaka)));
        areaList.add(new Area("2", "Paslaish", getString(R.string.paslaish)));
        areaList.add(new Area("2", "Pathantuli", getString(R.string.pathantuli)));
        areaList.add(new Area("2", "Potenga", getString(R.string.potenga)));
        areaList.add(new Area("2", "Patharghata", getString(R.string.patharghata)));
        areaList.add(new Area("2", "Pahartali", getString(R.string.pahartali)));
        areaList.add(new Area("2", "Purbonimtala", getString(R.string.purbo_nimtala)));
        areaList.add(new Area("2", "Purbomadarbari", getString(R.string.purbo_madar_bari)));
        areaList.add(new Area("2", "Bahaddarhat", getString(R.string.bahaddarhat)));
        areaList.add(new Area("2", "Bangladeshbankcolony", getString(R.string.bangladesh_bank_colony)));
        areaList.add(new Area("2", "bakolia", getString(R.string.bakolia)));
        areaList.add(new Area("2", "Bayazidbostami", getString(R.string.bayazid_bostami)));
        areaList.add(new Area("2", "Mansurabad", getString(R.string.mansurabad)));
        areaList.add(new Area("2", "Maijpara", getString(R.string.majipara)));
        areaList.add(new Area("2", "Rangiparabankcolony", getString(R.string.rangipara_bank_colony)));
        areaList.add(new Area("2", "Laldairchar", getString(R.string.laldair_char)));
        areaList.add(new Area("2", "Shadorghat", getString(R.string.shadorghat)));
        areaList.add(new Area("2", "Sondippara", getString(R.string.sondip_para)));
        areaList.add(new Area("2", "Southagrabad", getString(R.string.south_agrabad)));
        areaList.add(new Area("2", "CGScolony", getString(R.string.cgs_colony)));
        areaList.add(new Area("2", "Hali_shohor", getString(R.string.hali_shohor)));
        areaList.add(new Area("2", "Halishohormunshipara", getString(R.string.hali_shohor_munshipara)));
        areaList.add(new Area("2", "Halishohorsenanibash", getString(R.string.hali_shohor_senanibash)));
        areaList.add(new Area("2", "Hosenahmedpara", getString(R.string.hosen_ahmedpara)));


        areaList.add(new Area("Dhaka", "Kishorgonj", getString(R.string.kishorgonj)));
        areaList.add(new Area("Dhaka", "Gazipur", getString(R.string.gazipur)));
        areaList.add(new Area("Dhaka", "Gopalgonj", getString(R.string.gopalgonj)));
        areaList.add(new Area("Dhaka", "Tangail", getString(R.string.tangail)));
        areaList.add(new Area("Dhaka", "Dhaka", getString(R.string.dhaka)));
        areaList.add(new Area("Dhaka", "Narsingdi", getString(R.string.narsingdi)));
        areaList.add(new Area("Dhaka", "NarayanGanj", getString(R.string.narayanganj)));
        areaList.add(new Area("Dhaka", "Faridpur", getString(R.string.faridpur)));
        areaList.add(new Area("Dhaka", "Madaripur", getString(R.string.madaripur)));
        areaList.add(new Area("Dhaka", "Manikgonj", getString(R.string.manikgonj)));
        areaList.add(new Area("Dhaka", "Munshigonj", getString(R.string.munshigonj)));
        areaList.add(new Area("Dhaka", "Rajbari", getString(R.string.rajbari)));
        areaList.add(new Area("Dhaka", "Shariatpur", getString(R.string.shariatpur)));


        areaList.add(new Area("Barisal", "Jhalokati", getString(R.string.jhalokati)));
        areaList.add(new Area("Barisal", "Patuakhali", getString(R.string.patuakhali)));
        areaList.add(new Area("Barisal", "Pirojpur", getString(R.string.pirojpur)));
        areaList.add(new Area("Barisal", "Barguna", getString(R.string.barguna)));
        areaList.add(new Area("Barisal", "Barisal", getString(R.string.barisal)));
        areaList.add(new Area("Barisal", "Bhola", getString(R.string.bhola)));


        areaList.add(new Area("Chittagong", "CoxsBazar", getString(R.string.cox_bazar)));
        areaList.add(new Area("Chittagong", "Comilla", getString(R.string.comilla)));
        areaList.add(new Area("Chittagong", "Khagrasori", getString(R.string.khagrasori)));
        areaList.add(new Area("Chittagong", "Chittagong", getString(R.string.chattogram)));
        areaList.add(new Area("Chittagong", "Chandpur", getString(R.string.chandpur)));
        areaList.add(new Area("Chittagong", "Noakhali", getString(R.string.noakhali)));
        areaList.add(new Area("Chittagong", "Feni", getString(R.string.feni)));
        areaList.add(new Area("Chittagong", "Bandarban", getString(R.string.bandarban)));
        areaList.add(new Area("Chittagong", "Brahmanbaria", getString(R.string.brahmanbaria)));
        areaList.add(new Area("Chittagong", "Rangamati", getString(R.string.rangamati)));
        areaList.add(new Area("Chittagong", "Lokkhipur", getString(R.string.lokkhipur)));

        areaList.add(new Area("Mymensingh", "Jamalpur", getString(R.string.jamalpur)));
        areaList.add(new Area("Mymensingh", "Netrokona", getString(R.string.netrokona)));
        areaList.add(new Area("Mymensingh", "Mymensingh", getString(R.string.mymensingh)));
        areaList.add(new Area("Mymensingh", "Sherpur", getString(R.string.sherpur)));

        areaList.add(new Area("Khulna", "Kustia", getString(R.string.kustia)));
        areaList.add(new Area("Khulna", "Khulna", getString(R.string.khulna)));
        areaList.add(new Area("Khulna", "Chuadanga", getString(R.string.chuadanga)));
        areaList.add(new Area("Khulna", "Jhenaidah", getString(R.string.jhenaidah)));
        areaList.add(new Area("Khulna", "Norail", getString(R.string.norail)));
        areaList.add(new Area("Khulna", "Bagerhat", getString(R.string.bagerhat)));
        areaList.add(new Area("Khulna", "Magura", getString(R.string.magura)));
        areaList.add(new Area("Khulna", "Meherpur", getString(R.string.meherpur)));
        areaList.add(new Area("Khulna", "Jessore", getString(R.string.jessore)));
        areaList.add(new Area("Khulna", "Satkhira", getString(R.string.satkhira)));

        areaList.add(new Area("Rajshahi", "Chapainawabganj", getString(R.string.chapainawabganj)));
        areaList.add(new Area("Rajshahi", "Joypurhat", getString(R.string.joypurhat)));
        areaList.add(new Area("Rajshahi", "Nouga", getString(R.string.nouga)));
        areaList.add(new Area("Rajshahi", "Natore", getString(R.string.natore)));
        areaList.add(new Area("Rajshahi", "Pabna", getString(R.string.pabna)));
        areaList.add(new Area("Rajshahi", "Bagura", getString(R.string.bagura)));
        areaList.add(new Area("Rajshahi", "Rajshahi", getString(R.string.rajshahi)));
        areaList.add(new Area("Rajshahi", "Sirajgonj", getString(R.string.sirajgonj)));

        areaList.add(new Area("Rangpur", "Kurigram", getString(R.string.kurigram)));
        areaList.add(new Area("Rangpur", "Gaibandha", getString(R.string.gaibandha)));
        areaList.add(new Area("Rangpur", "Thakurgaon", getString(R.string.thakurgaon)));
        areaList.add(new Area("Rangpur", "Dinajpur", getString(R.string.dinajpur)));
        areaList.add(new Area("Rangpur", "Nilfamari", getString(R.string.nilfamari)));
        areaList.add(new Area("Rangpur", "Ponchogor", getString(R.string.ponchogor)));
        areaList.add(new Area("Rangpur", "Rangpur", getString(R.string.rangpur)));
        areaList.add(new Area("Rangpur", "Lalmoni", getString(R.string.lalmoni)));

        areaList.add(new Area("Sylhet", "Moulvibazar", getString(R.string.moulvibazar)));
        areaList.add(new Area("Sylhet", "Sylhet", getString(R.string.sylhet)));
        areaList.add(new Area("Sylhet", "Sunamgonj", getString(R.string.sunamganj)));
        areaList.add(new Area("Sylhet", "Habiganj", getString(R.string.habiganj)));

    }

    private void fillSubAreaList() {
        subAreaList = new ArrayList<>();
        subAreaList.add(new SubArea("Uttara", "Aisnubug", getString(R.string.aisnubug)));
        subAreaList.add(new SubArea("Uttara", "Ajompur", getString(R.string.ajompur)));
        subAreaList.add(new SubArea("Uttara", "Anurbug", getString(R.string.anurbug)));
        subAreaList.add(new SubArea("Uttara", "Abdullahpur", getString(R.string.abdullahpur)));
        subAreaList.add(new SubArea("Uttara", "Amtola", getString(R.string.amtola)));
        subAreaList.add(new SubArea("Uttara", "AshiyanCity", getString(R.string.asiyan_city)));
        subAreaList.add(new SubArea("Uttara", "Ahalia", getString(R.string.ahalia)));
        subAreaList.add(new SubArea("Uttara", "Uttorkhan", getString(R.string.uttorkhan)));
        subAreaList.add(new SubArea("Uttara", "Kaola", getString(R.string.kaola)));
        subAreaList.add(new SubArea("Uttara", "Kajibari", getString(R.string.kajibari)));
        subAreaList.add(new SubArea("Uttara", "Kamarpara", getString(R.string.kamarpara)));
        subAreaList.add(new SubArea("Uttara", "Khilkhet", getString(R.string.khilkhet)));
        subAreaList.add(new SubArea("Uttara", "Gaoyair", getString(R.string.gaoyair)));
        subAreaList.add(new SubArea("Uttara", "Joshimuddin", getString(R.string.joshimuddin)));
        subAreaList.add(new SubArea("Uttara", "DhakaAirport", getString(R.string.dhaka_airport)));
        subAreaList.add(new SubArea("Uttara", "SouthMollertech", getString(R.string.south_mollertech)));
        subAreaList.add(new SubArea("Uttara", "Dokkhinkhan", getString(R.string.dokkhinkhan)));
        subAreaList.add(new SubArea("Uttara", "Diyabari", getString(R.string.diyabari)));
        subAreaList.add(new SubArea("Uttara", "Deoyanpara", getString(R.string.deoyanpara)));
        subAreaList.add(new SubArea("Uttara", "Dhour", getString(R.string.dhour)));
        subAreaList.add(new SubArea("Uttara", "Noddapara", getString(R.string.noddapara)));
        subAreaList.add(new SubArea("Uttara", "NoyaNogor", getString(R.string.noya_nogor)));
        subAreaList.add(new SubArea("Uttara", "Fulbaria", getString(R.string.fulbaria)));
        subAreaList.add(new SubArea("Uttara", "RomnarTech", getString(R.string.romnar_tech)));
        subAreaList.add(new SubArea("Uttara", "Bepari Bari", getString(R.string.bepari_bari)));
        subAreaList.add(new SubArea("Uttara", "Vatira", getString(R.string.vatira)));
        subAreaList.add(new SubArea("Uttara", "Viyapara", getString(R.string.viyapara)));
        subAreaList.add(new SubArea("Uttara", "ModdhoPara", getString(R.string.moddho_para)));
        subAreaList.add(new SubArea("Uttara", "MadarBari", getString(R.string.madar_bari)));
        subAreaList.add(new SubArea("Uttara", "RajBari", getString(R.string.raj_bari)));
        subAreaList.add(new SubArea("Uttara", "Shekherpara", getString(R.string.shekherpara)));
        subAreaList.add(new SubArea("Uttara", "Shonakhola", getString(R.string.shona_khola)));
        subAreaList.add(new SubArea("Uttara", "Hajipara", getString(R.string.hajipara)));
        subAreaList.add(new SubArea("Uttara", "1nosectorUttara", getString(R.string.viyapara)));
        subAreaList.add(new SubArea("Uttara", "10 no. sector Uttara", getString(R.string._10_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "11 no. sector Uttara", getString(R.string._11_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "12 no. sector Uttara", getString(R.string._12_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "13 no. sector Uttara", getString(R.string._13_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "14 no. sector Uttara", getString(R.string._14_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "15 no. sector Uttara", getString(R.string._15_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "16 no. sector Uttara", getString(R.string._16_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "17 no. sector Uttara", getString(R.string._17_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "2 no. sector Uttara", getString(R.string._2_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "3 no. sector Uttara", getString(R.string._3_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "4 no. sector Uttara", getString(R.string._4_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "5 no. sector Uttara", getString(R.string._5_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "6 no. sector Uttara", getString(R.string._6_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "7 no. sector Uttara", getString(R.string._7_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "8 no. sector Uttara", getString(R.string._8_no_sector_uttara)));
        subAreaList.add(new SubArea("Uttara", "9 no. sector Uttara", getString(R.string._9_no_sector_uttara)));


        subAreaList.add(new SubArea("Kafrul", "Ajijpolli", getString(R.string.ajij_polli)));
        subAreaList.add(new SubArea("Kafrul", "Dhaka navy colony", getString(R.string.dhaka_navy_colony)));
        subAreaList.add(new SubArea("Kafrul", "Damal kot", getString(R.string.dhamal_kot)));
        subAreaList.add(new SubArea("Kafrul", "Vasantech", getString(R.string.vasantech)));
        subAreaList.add(new SubArea("Kafrul", "Mohakhali dohs", getString(R.string.mohakhali_dohs)));
        subAreaList.add(new SubArea("Kafrul", "Matikata", getString(R.string.matikata)));
        subAreaList.add(new SubArea("Kafrul", "Soudi koloni", getString(R.string.soudi_koloni)));


        subAreaList.add(new SubArea("KamrangirChor", "Abu soiyod bazar", getString(R.string.abu_soiyod_bazar)));
        subAreaList.add(new SubArea("KamrangirChor", "Koyla ghat", getString(R.string.koyla_ghat)));
        subAreaList.add(new SubArea("KamrangirChor", "Karim bug", getString(R.string.karim_bug)));
        subAreaList.add(new SubArea("KamrangirChor", "Nurburg", getString(R.string.nurbug)));
        subAreaList.add(new SubArea("KamrangirChor", "Munsi hut", getString(R.string.munshi_hat)));
        subAreaList.add(new SubArea("KamrangirChor", "Muslimbug", getString(R.string.muslimbug)));
        subAreaList.add(new SubArea("KamrangirChor", "Zaullar hati chourasta", getString(R.string.zaullar_hati_chourasta)));
        subAreaList.add(new SubArea("KamrangirChor", "Sultanganj", getString(R.string.sultanganj)));
        subAreaList.add(new SubArea("KamrangirChor", "Hajurpara", getString(R.string.hajirpara)));
        subAreaList.add(new SubArea("KamrangirChor", "Hasan nagor", getString(R.string.hasan_nagor)));


        subAreaList.add(new SubArea("Kotoyaly", "Islampur", getString(R.string.islampur)));
        subAreaList.add(new SubArea("Kotoyaly", "Jinda bazar", getString(R.string.jinda_bazar)));
        subAreaList.add(new SubArea("Kotoyaly", "Tati bazar", getString(R.string.tati_bazar)));
        subAreaList.add(new SubArea("Kotoyaly", "Noya bazar", getString(R.string.noya_bazar)));
        subAreaList.add(new SubArea("Kotoyaly", "Potuyatoli", getString(R.string.potuyatoli)));
        subAreaList.add(new SubArea("Kotoyaly", "Badam toli", getString(R.string.badam_toli)));
        subAreaList.add(new SubArea("Kotoyaly", "Ray saheb bazar", getString(R.string.ray_ssaheb_bazar)));
        subAreaList.add(new SubArea("Kotoyaly", "Lokkhi Bazar", getString(R.string.lokkhi_bazar)));
        subAreaList.add(new SubArea("Kotoyaly", "Sadarghat", getString(R.string.sadarghat)));


        subAreaList.add(new SubArea("Cantonment", "Ecb chottor", getString(R.string.ecb_chottor)));
        subAreaList.add(new SubArea("Cantonment", "MES colony", getString(R.string.mes_colony)));
        subAreaList.add(new SubArea("Cantonment", "Kalibari", getString(R.string.kalibari)));
        subAreaList.add(new SubArea("Cantonment", "Noya bazar", getString(R.string.noya_bazar)));
        subAreaList.add(new SubArea("Cantonment", "Kurmitola", getString(R.string.kirmitola)));
        subAreaList.add(new SubArea("Cantonment", "Goaltech", getString(R.string.goaltech)));
        subAreaList.add(new SubArea("Cantonment", "Jamtola", getString(R.string.jamtola)));
        subAreaList.add(new SubArea("Cantonment", "Zia colony", getString(R.string.zia_colony)));
        subAreaList.add(new SubArea("Cantonment", "Dali market", getString(R.string.dali_market)));
        subAreaList.add(new SubArea("Cantonment", "Deoyanpara", getString(R.string.deoyanpara)));
        subAreaList.add(new SubArea("Cantonment", "Nikung", getString(R.string.nikung)));
        subAreaList.add(new SubArea("Cantonment", "Nirjhor", getString(R.string.nirjhor)));
        subAreaList.add(new SubArea("Cantonment", "Barontech", getString(R.string.barontech)));
        subAreaList.add(new SubArea("Cantonment", "Balurghat", getString(R.string.balurghat)));
        subAreaList.add(new SubArea("Cantonment", "Manikdi", getString(R.string.manikdi)));
        subAreaList.add(new SubArea("Cantonment", "Mastertech", getString(R.string.mastertech)));
        subAreaList.add(new SubArea("Cantonment", "Mostafa kamal chottor", getString(R.string.mostafa_kamal_chottor)));


        subAreaList.add(new SubArea("Khilgaon", "Adarsh bug", getString(R.string.adarsh)));
        subAreaList.add(new SubArea("Khilgaon", "Ansar bug", getString(R.string.ansar_bug)));
        subAreaList.add(new SubArea("Khilgaon", "Aftab Nagar", getString(R.string.aftab_nagar)));
        subAreaList.add(new SubArea("Khilgaon", "Ulon", getString(R.string.ulon)));
        subAreaList.add(new SubArea("Khilgaon", "Khilgaon bagicha", getString(R.string.khilgoan_bagicha)));
        subAreaList.add(new SubArea("Khilgaon", "Khilgaon block -a", getString(R.string.khilgoan_block_a)));
        subAreaList.add(new SubArea("Khilgaon", "Khilgaon block -b", getString(R.string.khilgoan_block_b)));
        subAreaList.add(new SubArea("Khilgaon", "Khilgaon block -c", getString(R.string.khilgoan_block_c)));
        subAreaList.add(new SubArea("Khilgaon", "Khilgaon railgate", getString(R.string.khilgoan_railgate)));
        subAreaList.add(new SubArea("Khilgaon", "Goran", getString(R.string.goran)));
        subAreaList.add(new SubArea("Khilgaon", "Chowdhury Para", getString(R.string.chowdhury_para)));
        subAreaList.add(new SubArea("Khilgaon", "Tilpa para", getString(R.string.tilpa_para)));
        subAreaList.add(new SubArea("Khilgaon", "Notun bug", getString(R.string.notun_bug)));
        subAreaList.add(new SubArea("Khilgaon", "Nondi para", getString(R.string.nondi_para)));
        subAreaList.add(new SubArea("Khilgaon", "Bonosri", getString(R.string.bonosri)));
        subAreaList.add(new SubArea("Khilgaon", "Malibog", getString(R.string.malibug)));
        subAreaList.add(new SubArea("Khilgaon", "Meradiya", getString(R.string.meradiya)));
        subAreaList.add(new SubArea("Khilgaon", "Rampura", getString(R.string.rampura)));
        subAreaList.add(new SubArea("Khilgaon", "Riyaj bug", getString(R.string.riyai_bug)));
        subAreaList.add(new SubArea("Khilgaon", "Sipahi Bug", getString(R.string.sipahi_bug)));


        subAreaList.add(new SubArea("Gulshan", "Korail", getString(R.string.korail)));
        subAreaList.add(new SubArea("Gulshan", "Gulshan avenue", getString(R.string.gulshan_avenue)));
        subAreaList.add(new SubArea("Gulshan", "Gulshan 2", getString(R.string.gulshan_2)));
        subAreaList.add(new SubArea("Gulshan", "Gulshan 1", getString(R.string.gulshan_1)));
        subAreaList.add(new SubArea("Gulshan", "Niketon", getString(R.string.niketon)));
        subAreaList.add(new SubArea("Gulshan", "Bonani", getString(R.string.bonani)));
        subAreaList.add(new SubArea("Gulshan", "Bari dhara", getString(R.string.bari_dhara)));
        subAreaList.add(new SubArea("Gulshan", "Bari dhara dohs", getString(R.string.bari_dhara_dohs)));
        subAreaList.add(new SubArea("Gulshan", "Mohakhali", getString(R.string.mohakhali)));


        subAreaList.add(new SubArea("Demra", "Ahmed Nagar", getString(R.string.ahmed_nagar)));
        subAreaList.add(new SubArea("Demra", "Konapara", getString(R.string.konapara)));
        subAreaList.add(new SubArea("Demra", "Green model town", getString(R.string.green_model_town)));
        subAreaList.add(new SubArea("Demra", "Chonpara", getString(R.string.chonpara)));
        subAreaList.add(new SubArea("Demra", "Dogair", getString(R.string.dogair)));
        subAreaList.add(new SubArea("Demra", "Tarabo", getString(R.string.tarabo)));
        subAreaList.add(new SubArea("Demra", "South rupsi", getString(R.string.south_rupsi)));
        subAreaList.add(new SubArea("Demra", "Naopara", getString(R.string.naopara)));
        subAreaList.add(new SubArea("Demra", "Bamoli bazar", getString(R.string.bamoli_bazar)));
        subAreaList.add(new SubArea("Demra", "Bahir tengra", getString(R.string.bahir_tengra)));
        subAreaList.add(new SubArea("Demra", "Rasul nagor", getString(R.string.rasul_nagor)));
        subAreaList.add(new SubArea("Demra", "Sanar para", getString(R.string.saar_para)));
        subAreaList.add(new SubArea("Demra", "Saruliya", getString(R.string.saruliya)));
        subAreaList.add(new SubArea("Demra", "Haji nagor", getString(R.string.haji_nagor)));


        subAreaList.add(new SubArea("Tejgaon", "Kawran Bazar", getString(R.string.kawran_bazar)));
        subAreaList.add(new SubArea("Tejgaon", "Tejkuni para", getString(R.string.tejkuni_para)));
        subAreaList.add(new SubArea("Tejgaon", "Nakhal para", getString(R.string.nakhal_para)));
        subAreaList.add(new SubArea("Tejgaon", "Tejturi bazar", getString(R.string.tejturi_bazar)));
        subAreaList.add(new SubArea("Tejgaon", "Nabisko", getString(R.string.nabisko)));
        subAreaList.add(new SubArea("Tejgaon", "Farmgate", getString(R.string.farmgate)));
        subAreaList.add(new SubArea("Tejgaon", "Monipuripara", getString(R.string.monipuripara)));
        subAreaList.add(new SubArea("Tejgaon", "Rasul bug", getString(R.string.rsul_bug)));
        subAreaList.add(new SubArea("Tejgaon", "Raja bazar", getString(R.string.raja_bazar)));
        subAreaList.add(new SubArea("Tejgaon", "Shahin bug", getString(R.string.shahin_bug)));
        subAreaList.add(new SubArea("Tejgaon", "Shukrabad", getString(R.string.shukrabad)));


        subAreaList.add(new SubArea("Dhanmondi", "Kalabagan", getString(R.string.kalabagan)));
        subAreaList.add(new SubArea("Dhanmondi", "Green road", getString(R.string.green_road)));
        subAreaList.add(new SubArea("Dhanmondi", "Jiga tola", getString(R.string.jiga_tola)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -1", getString(R.string.dhanmondi_1)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -2", getString(R.string.dhanmondi_2)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -3", getString(R.string.dhanmondi_3)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -4", getString(R.string.dhanmondi_4)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -5", getString(R.string.dhanmondi_5)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -6", getString(R.string.dhanmondi_6)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -7", getString(R.string.dhanmondi_7)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -8A", getString(R.string.dhanmondi_8a)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -9A", getString(R.string.dhanmondi_9a)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -10A", getString(R.string.dhanmondi_10a)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -11", getString(R.string.dhanmondi_11)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -12", getString(R.string.dhanmondi_12)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -13", getString(R.string.dhanmondi_13)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -14", getString(R.string.dhanmondi_14)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -15", getString(R.string.dhanmondi_15)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -27", getString(R.string.dhanmondi_27)));
        subAreaList.add(new SubArea("Dhanmondi", "Dhanmondi -32", getString(R.string.dhanmondi_32)));
        subAreaList.add(new SubArea("Dhanmondi", "Newmarket", getString(R.string.newmarket)));
        subAreaList.add(new SubArea("Dhanmondi", "Panthapath", getString(R.string.panthapath)));
        subAreaList.add(new SubArea("Dhanmondi", "Shankar", getString(R.string.kalashankarbagan)));
        subAreaList.add(new SubArea("Dhanmondi", "Shobhan Bug", getString(R.string.shobhan_bug)));
        subAreaList.add(new SubArea("Dhanmondi", "Shukrabad", getString(R.string.shukrabad)));


        subAreaList.add(new SubArea("Pollobi", "Kalshi", getString(R.string.kalshi)));
        subAreaList.add(new SubArea("Pollobi", "Duip nagar", getString(R.string.duip_nagar)));
        subAreaList.add(new SubArea("Pollobi", "Bordhito polli", getString(R.string.bordhito_polli)));
        subAreaList.add(new SubArea("Pollobi", "Mirpur dohs", getString(R.string.mirpur_dohs)));
        subAreaList.add(new SubArea("Pollobi", "Mirpur senanibas", getString(R.string.mirpur_senanibas)));
        subAreaList.add(new SubArea("Pollobi", "Shagufta", getString(R.string.shagufta)));
        subAreaList.add(new SubArea("Pollobi", "Section-12 mirpur", getString(R.string.section_12_mirpur)));


        subAreaList.add(new SubArea("Bosundhara", "Kuril", getString(R.string.kuril)));
        subAreaList.add(new SubArea("Bosundhara", "Cocacola", getString(R.string.cococola)));
        subAreaList.add(new SubArea("Bosundhara", "Nodda", getString(R.string.nodda)));
        subAreaList.add(new SubArea("Bosundhara", "Neela market", getString(R.string.neela_market)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -i", getString(R.string.bashundhara_block_i)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -e", getString(R.string.bashundhara_block_e)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -A", getString(R.string.bashundhara_block_a)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -H", getString(R.string.bashundhara_block_h)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -F", getString(R.string.bashundhara_block_f)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -M", getString(R.string.bashundhara_block_m)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -L", getString(R.string.bashundhara_block_L)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -K", getString(R.string.bashundhara_block_k)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -g", getString(R.string.bashundhara_block_g)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -j", getString(R.string.bashundhara_block_j)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -d", getString(R.string.bashundhara_block_d)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -b", getString(R.string.bashundhara_block_b)));
        subAreaList.add(new SubArea("Bosundhara", "Bashundhara block -c", getString(R.string.bashundhara_block_c)));
        subAreaList.add(new SubArea("Bosundhara", "Beraid", getString(R.string.beraid)));


        subAreaList.add(new SubArea("Badda", "Namapara", getString(R.string.namapara)));
        subAreaList.add(new SubArea("Badda", "Nurerchala", getString(R.string.benurerchalaraid)));
        subAreaList.add(new SubArea("Badda", "East nurer chala", getString(R.string.east_nurer_chala)));
        subAreaList.add(new SubArea("Badda", "Bayou La para", getString(R.string.beyou_la_para)));
        subAreaList.add(new SubArea("Badda", "Badda D I T project", getString(R.string.badda_dit_project)));
        subAreaList.add(new SubArea("Badda", "Badda link road", getString(R.string.badda_link_road)));
        subAreaList.add(new SubArea("Badda", "Bepari bari", getString(R.string.bepari_bari)));
        subAreaList.add(new SubArea("Badda", "Middle badda", getString(R.string.middle_badda)));
        subAreaList.add(new SubArea("Badda", "Merul", getString(R.string.merul)));
        subAreaList.add(new SubArea("Badda", "Vatara", getString(R.string.vatara)));
        subAreaList.add(new SubArea("Badda", "Merul badda", getString(R.string.merul_badda)));
        subAreaList.add(new SubArea("Badda", "Rup nagar", getString(R.string.rup_nagar)));
        subAreaList.add(new SubArea("Badda", "Shahjadpur", getString(R.string.shahjadpur)));
        subAreaList.add(new SubArea("Badda", "Solmaid", getString(R.string.solmaid)));


        subAreaList.add(new SubArea("Motijhil", "Fakirapul", getString(R.string.fakirapul)));
        subAreaList.add(new SubArea("Motijhil", "Motijhil", getString(R.string.motijhil)));
        subAreaList.add(new SubArea("Motijhil", "Razarbug", getString(R.string.razarbug)));
        subAreaList.add(new SubArea("Motijhil", "Shahidbug", getString(R.string.shahidbug)));
        subAreaList.add(new SubArea("Motijhil", "Shanti nagar", getString(R.string.bershanti_nagaraid)));
        subAreaList.add(new SubArea("Motijhil", "Shanti bug", getString(R.string.shanti_bug)));
        subAreaList.add(new SubArea("Motijhil", "Shahjahanpur", getString(R.string.shahjahanpur)));


        subAreaList.add(new SubArea("Mirpur", "Gabtoli", getString(R.string.gabtoli)));
        subAreaList.add(new SubArea("Mirpur", "Zoo", getString(R.string.zoo)));
        subAreaList.add(new SubArea("Mirpur", "Zahurabad", getString(R.string.zahurabad)));
        subAreaList.add(new SubArea("Mirpur", "Tolarbug", getString(R.string.tolarbug)));
        subAreaList.add(new SubArea("Mirpur", "South monipur", getString(R.string.south_monipur)));
        subAreaList.add(new SubArea("Mirpur", "Nonderbug", getString(R.string.nonderbug)));
        subAreaList.add(new SubArea("Mirpur", "Nobaberbug", getString(R.string.nobaberbug)));
        subAreaList.add(new SubArea("Mirpur", "West kazipara", getString(R.string.west_kazipara)));
        subAreaList.add(new SubArea("Mirpur", "West shewrapara", getString(R.string.west_shewrapara)));
        subAreaList.add(new SubArea("Mirpur", "Paikpara", getString(R.string.paikpara)));
        subAreaList.add(new SubArea("Mirpur", "Palapara", getString(R.string.palapara)));
        subAreaList.add(new SubArea("Mirpur", "Pirer bug", getString(R.string.pirerbug)));
        subAreaList.add(new SubArea("Mirpur", "East kazipara", getString(R.string.est_kazipara)));
        subAreaList.add(new SubArea("Mirpur", "East shewrapara", getString(R.string.east_shewrapara)));
        subAreaList.add(new SubArea("Mirpur", "Boshupara", getString(R.string.boshupara)));
        subAreaList.add(new SubArea("Mirpur", "Baten nagar", getString(R.string.baten_nagar)));
        subAreaList.add(new SubArea("Mirpur", "Beribadh", getString(R.string.beribadh)));
        subAreaList.add(new SubArea("Mirpur", "Borobug", getString(R.string.borobug)));
        subAreaList.add(new SubArea("Mirpur", "Modina nagar", getString(R.string.modina_nagar)));
        subAreaList.add(new SubArea("Mirpur", "Monipur", getString(R.string.monipur)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -6", getString(R.string.mirpur_6)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -2", getString(R.string.mirpur_2)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -60 feet", getString(R.string.mirpur_60_feet)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur rupnagar abashik alaka", getString(R.string.mirpur_rupnagar_abashik_alaka)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -11", getString(R.string.mirpur_11)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -12", getString(R.string.mirpur_12)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -10", getString(R.string.mirpur_10)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -13", getString(R.string.mirpur_13)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -14", getString(R.string.mirpur_14)));
        subAreaList.add(new SubArea("Mirpur", "Mirpur -1", getString(R.string.mirpur_1)));
        subAreaList.add(new SubArea("Mirpur", "Mohammadiya society", getString(R.string.mohammadiya_society)));
        subAreaList.add(new SubArea("Mirpur", "Rainkhola", getString(R.string.rainlhola)));
        subAreaList.add(new SubArea("Mirpur", "Shain pukur", getString(R.string.shain_pukur)));
        subAreaList.add(new SubArea("Mirpur", "Shah ali bug", getString(R.string.shah_ali_bug)));
        subAreaList.add(new SubArea("Mirpur", "Shimul tala", getString(R.string.shimul_tala)));
        subAreaList.add(new SubArea("Mirpur", "Lalkuthi", getString(R.string.lalkuthi)));
        subAreaList.add(new SubArea("Mirpur", "Hariram pur", getString(R.string.hariram_pur)));


        subAreaList.add(new SubArea("Mohammadpur", "Adabar", getString(R.string.adabar)));
        subAreaList.add(new SubArea("Mohammadpur", "Ashadget", getString(R.string.ashadget)));
        subAreaList.add(new SubArea("Mohammadpur", "Chadmiya housing", getString(R.string.chadmiya_housing)));
        subAreaList.add(new SubArea("Mohammadpur", "Jafrabad", getString(R.string.jafrabad)));
        subAreaList.add(new SubArea("Mohammadpur", "Tajmohol road", getString(R.string.tajmohol_road)));
        subAreaList.add(new SubArea("Mohammadpur", "Bosila", getString(R.string.bosila)));
        subAreaList.add(new SubArea("Mohammadpur", "Baitul aman housing", getString(R.string.baitul_aman_housing)));
        subAreaList.add(new SubArea("Mohammadpur", "Mohammadiya abashik alaka", getString(R.string.mohammadiya_abashik_alaka)));
        subAreaList.add(new SubArea("Mohammadpur", "Ring road", getString(R.string.ringroad)));
        subAreaList.add(new SubArea("Mohammadpur", "Lalmatia", getString(R.string.lalmatia)));
        subAreaList.add(new SubArea("Mohammadpur", "Shongkar", getString(R.string.shongkar)));
        subAreaList.add(new SubArea("Mohammadpur", "Shekhertek", getString(R.string.shekhertek)));
        subAreaList.add(new SubArea("Mohammadpur", "Shamoli", getString(R.string.shamoli)));


        subAreaList.add(new SubArea("Ramona", "Elephant road", getString(R.string.elephant_road)));
        subAreaList.add(new SubArea("Ramona", "Old iskaton", getString(R.string.old_iskaton)));
        subAreaList.add(new SubArea("Ramona", "Kakrail", getString(R.string.kakrail)));
        subAreaList.add(new SubArea("Ramona", "Katabon", getString(R.string.katabon)));
        subAreaList.add(new SubArea("Ramona", "Dhaka varsity", getString(R.string.dhaka_versity)));
        subAreaList.add(new SubArea("Ramona", "New iskaton", getString(R.string.new_iskaton)));
        subAreaList.add(new SubArea("Ramona", "Naya Tola", getString(R.string.noya_tola)));
        subAreaList.add(new SubArea("Ramona", "Poribug", getString(R.string.poribug)));
        subAreaList.add(new SubArea("Ramona", "Bangla motor", getString(R.string.bangla_motor)));
        subAreaList.add(new SubArea("Ramona", "Beili road", getString(R.string.beili_road)));
        subAreaList.add(new SubArea("Ramona", "Boro mogbazar", getString(R.string.boro_mogbazar)));
        subAreaList.add(new SubArea("Ramona", "Moghbazar T & T colony", getString(R.string.mogbazar_tandt_colony)));
        subAreaList.add(new SubArea("Ramona", "Mirbug", getString(R.string.mirbug)));
        subAreaList.add(new SubArea("Ramona", "Shah bug", getString(R.string.shahbug)));
        subAreaList.add(new SubArea("Ramona", "Shiddessori", getString(R.string.shiddessori)));
        subAreaList.add(new SubArea("Ramona", "Segunbagicha", getString(R.string.segunbagicha)));
        subAreaList.add(new SubArea("Ramona", "Hatirpul", getString(R.string.hatirpul)));


        subAreaList.add(new SubArea("Lalbag", "Azimpur", getString(R.string.azimpur)));
        subAreaList.add(new SubArea("Lalbag", "Amligola", getString(R.string.amligola)));
        subAreaList.add(new SubArea("Lalbag", "Islam bug", getString(R.string.islambug)));
        subAreaList.add(new SubArea("Lalbag", "Kamrangirchor", getString(R.string.kamrangirchor)));
        subAreaList.add(new SubArea("Lalbag", "Chawkbazar", getString(R.string.chawkbazar)));
        subAreaList.add(new SubArea("Lalbag", "Bakshi bazar", getString(R.string.bakshibazar)));
        subAreaList.add(new SubArea("Lalbag", "Babu bazar", getString(R.string.babubazar)));
        subAreaList.add(new SubArea("Lalbag", "Begum bazar", getString(R.string.begumbazar)));
        subAreaList.add(new SubArea("Lalbag", "Lalbug", getString(R.string.lalbug)));
        subAreaList.add(new SubArea("Lalbag", "Shahid nagar", getString(R.string.shahid_nagar)));
        subAreaList.add(new SubArea("Lalbag", "Soyari ghat", getString(R.string.soyari_ghat)));


        subAreaList.add(new SubArea("Shampur", "Kadamtali", getString(R.string.kadamtali)));
        subAreaList.add(new SubArea("Shampur", "Kutub khali", getString(R.string.kutub_khali)));
        subAreaList.add(new SubArea("Shampur", "Janata market", getString(R.string.janata_market)));
        subAreaList.add(new SubArea("Shampur", "Japani market", getString(R.string.japani_market)));
        subAreaList.add(new SubArea("Shampur", "Jurain", getString(R.string.jurain)));
        subAreaList.add(new SubArea("Shampur", "Doniya", getString(R.string.doniya)));
        subAreaList.add(new SubArea("Shampur", "Deul para", getString(R.string.deul_para)));
        subAreaList.add(new SubArea("Shampur", "Dholai para", getString(R.string.dholaipara)));
        subAreaList.add(new SubArea("Shampur", "Pagla", getString(R.string.pagla)));
        subAreaList.add(new SubArea("Shampur", "Faridabad", getString(R.string.faridabad)));
        subAreaList.add(new SubArea("Shampur", "Munshibug", getString(R.string.munshibug)));
        subAreaList.add(new SubArea("Shampur", "Muradpur", getString(R.string.muradpur)));
        subAreaList.add(new SubArea("Shampur", "Meraz nagar", getString(R.string.meraznagar)));
        subAreaList.add(new SubArea("Shampur", "Mohammad Bug", getString(R.string.mohammadbugg)));
        subAreaList.add(new SubArea("Shampur", "Rasulpur", getString(R.string.rasulpur)));
        subAreaList.add(new SubArea("Shampur", "Rayer bug", getString(R.string.rayerbug)));


        subAreaList.add(new SubArea("ShobujBug", "Ahmed Bug", getString(R.string.ahmedbug)));
        subAreaList.add(new SubArea("ShobujBug", "North mugda para", getString(R.string.north_mugda_para)));
        subAreaList.add(new SubArea("ShobujBug", "Kadamtala", getString(R.string.kadamtala)));
        subAreaList.add(new SubArea("ShobujBug", "South mugdapara", getString(R.string.south_mugdapara)));
        subAreaList.add(new SubArea("ShobujBug", "Natun para", getString(R.string.natunpara)));
        subAreaList.add(new SubArea("ShobujBug", "East nandipara", getString(R.string.east_nandipara)));
        subAreaList.add(new SubArea("ShobujBug", "Baganbari", getString(R.string.baganbari)));
        subAreaList.add(new SubArea("ShobujBug", "Basabo", getString(R.string.basabo)));
        subAreaList.add(new SubArea("ShobujBug", "Madartech", getString(R.string.madartech)));
        subAreaList.add(new SubArea("ShobujBug", "Mugdapara", getString(R.string.mugdapara)));

        subAreaList.add(new SubArea("Sutrapur", "Wari", getString(R.string.wari)));
        subAreaList.add(new SubArea("Sutrapur", "Kaptan bazar", getString(R.string.kaptan_bazar)));
        subAreaList.add(new SubArea("Sutrapur", "Kerati tola", getString(R.string.keraati_tola)));
        subAreaList.add(new SubArea("Sutrapur", "Gendaria", getString(R.string.gendaria)));
        subAreaList.add(new SubArea("Sutrapur", "Tikatuli", getString(R.string.tikatuli)));
        subAreaList.add(new SubArea("Sutrapur", "Doyagang", getString(R.string.doyagang)));
        subAreaList.add(new SubArea("Sutrapur", "Dhup khola", getString(R.string.dhup_khola)));
        subAreaList.add(new SubArea("Sutrapur", "Nawabpur", getString(R.string.nawabpur)));
        subAreaList.add(new SubArea("Sutrapur", "Narinda", getString(R.string.narinda)));
        subAreaList.add(new SubArea("Sutrapur", "Jatrabari", getString(R.string.jatrabari)));
        subAreaList.add(new SubArea("Sutrapur", "Saydabad", getString(R.string.saydabad)));
        subAreaList.add(new SubArea("Sutrapur", "Soyami bug", getString(R.string.soyamibug)));
        subAreaList.add(new SubArea("Sutrapur", "Hut khola", getString(R.string.hatkhola)));


        subAreaList.add(new SubArea("Hajaribug", "Anayet Gang", getString(R.string.anayetgang)));
        subAreaList.add(new SubArea("Hajaribug", "Company ghat", getString(R.string.company_ghat)));
        subAreaList.add(new SubArea("Hajaribug", "Jigatola", getString(R.string.jigatola)));
        subAreaList.add(new SubArea("Hajaribug", "Tollabug", getString(R.string.tollabug)));
        subAreaList.add(new SubArea("Hajaribug", "Tinmazar", getString(R.string.tinmazar)));
        subAreaList.add(new SubArea("Hajaribug", "Nabab gang", getString(R.string.nababganj)));
        subAreaList.add(new SubArea("Hajaribug", "Pilkhana", getString(R.string.pilkhana)));
        subAreaList.add(new SubArea("Hajaribug", "Borhanpur", getString(R.string.borhanpur)));
        subAreaList.add(new SubArea("Hajaribug", "Monesshor", getString(R.string.monesshor)));
        subAreaList.add(new SubArea("Hajaribug", "Rayer bazar", getString(R.string.rayerbazar)));
        subAreaList.add(new SubArea("Hajaribug", "Hazari bug", getString(R.string.hazaribug)));


        subAreaList.add(new SubArea("Akborshah", "Alongkar mor", getString(R.string.alongkarmor)));
        subAreaList.add(new SubArea("Akborshah", "Ishapani mor", getString(R.string.ishapanimor)));
        subAreaList.add(new SubArea("Akborshah", "Ak khan", getString(R.string.akkhan)));
        subAreaList.add(new SubArea("Akborshah", "Karnel hat", getString(R.string.karnelhat)));
        subAreaList.add(new SubArea("Akborshah", "Kalir choura", getString(R.string.kalir_choura)));
        subAreaList.add(new SubArea("Akborshah", "Kiobolo badh railway station", getString(R.string.kiobolo_badh_railway_station)));
        subAreaList.add(new SubArea("Akborshah", "Josim market", getString(R.string.josim_market)));
        subAreaList.add(new SubArea("Akborshah", "Pahartoli", getString(R.string.pahartoli)));
        subAreaList.add(new SubArea("Akborshah", "Purobi feroz shah mazar", getString(R.string.purobi_feroz_shah_mazar)));
        subAreaList.add(new SubArea("Akborshah", "Firoz shah colony", getString(R.string.firoz_shah_colony)));
        subAreaList.add(new SubArea("Akborshah", "CDA 1 no bus stop", getString(R.string.cda_1_no_bus_stop)));


        subAreaList.add(new SubArea("Andharkilla", "Andhar killa", getString(R.string.andhar_killa)));


        subAreaList.add(new SubArea("AmirbugAbashikalaka", "Amirbug Abashik alaka", getString(R.string.amirbug_abashik_alaka)));


        subAreaList.add(new SubArea("Alishapara", "Ali shapara", getString(R.string.ali_shapara)));

        subAreaList.add(new SubArea("Asadgonjcommercialarea", "Asadgonj commercial area", getString(R.string.asadganj_commercial_area)));


        subAreaList.add(new SubArea("EPZ", "Ishan mistrir hat", getString(R.string.ishan_mistrir_hat)));
        subAreaList.add(new SubArea("EPZ", "Hritu hostel", getString(R.string.hritu_hostel)));
        subAreaList.add(new SubArea("EPZ", "Chowdhury bazar", getString(R.string.chowdhury_bazar)));
        subAreaList.add(new SubArea("EPZ", "Dhup pal", getString(R.string.dhup_pal)));
        subAreaList.add(new SubArea("EPZ", "Nim tola", getString(R.string.nim_tola)));
        subAreaList.add(new SubArea("EPZ", "Bandortila", getString(R.string.bandortila)));
        subAreaList.add(new SubArea("EPZ", "BNA office", getString(R.string.bna_office)));
        subAreaList.add(new SubArea("EPZ", "BNA upanibesh", getString(R.string.bna_upanibesh)));
        subAreaList.add(new SubArea("EPZ", "Bissho rasta", getString(R.string.bissho_rasta)));
        subAreaList.add(new SubArea("EPZ", "Lohar pul", getString(R.string.lohar_pul)));
        subAreaList.add(new SubArea("EPZ", "Saltgola", getString(R.string.saltgola)));
        subAreaList.add(new SubArea("EPZ", "Sagorika", getString(R.string.sagorika)));
        subAreaList.add(new SubArea("EPZ", "CEPZ mailer matha", getString(R.string.cepz_mailer_matha)));
        subAreaList.add(new SubArea("EPZ", "Cement crossing", getString(R.string.cement_crossing)));
        subAreaList.add(new SubArea("EPZ", "Steel mil bazar", getString(R.string.steel_mil_bazar)));
        subAreaList.add(new SubArea("EPZ", "Haspataler get", getString(R.string.haspataler_get)));
        subAreaList.add(new SubArea("EPZ", "3 no fakir hat", getString(R.string._3_no_fakir_hat)));


        subAreaList.add(new SubArea("Uttarpathantuli", "Uttar pathantuli", getString(R.string.uttar_pathantuli)));

        subAreaList.add(new SubArea("Anayetbazar", "Anayet bazar", getString(R.string.anayet_bazar)));
        subAreaList.add(new SubArea("KarnafuliResidentialarea", "Karnafuli Residential area", getString(R.string.karnafuli_residential_area)));


        subAreaList.add(new SubArea("Kotoyali", "Amirbug", getString(R.string.amirbug)));
        subAreaList.add(new SubArea("Kotoyali", "Kajir deuri", getString(R.string.kajir_deuri)));
        subAreaList.add(new SubArea("Kotoyali", "Khatun ganj", getString(R.string.khatun_ganj)));
        subAreaList.add(new SubArea("Kotoyali", "Gani uponibesh", getString(R.string.gani_uponibesh)));
        subAreaList.add(new SubArea("Kotoyali", "Chattagram bandor", getString(R.string.chattagram_bandor)));
        subAreaList.add(new SubArea("Kotoyali", "Jaotola", getString(R.string.jaotola)));
        subAreaList.add(new SubArea("Kotoyali", "Firingi bazar", getString(R.string.firingi_bazar)));
        subAreaList.add(new SubArea("Kotoyali", "Pathorghat", getString(R.string.pathorghat)));
        subAreaList.add(new SubArea("Kotoyali", "Pathantuli", getString(R.string.pathantuli)));
        subAreaList.add(new SubArea("Kotoyali", "Station road", getString(R.string.station_road)));
        subAreaList.add(new SubArea("Kotoyali", "Stand rasta", getString(R.string.stand_rasta)));

        subAreaList.add(new SubArea("Khatungonj", "Khatungonj", getString(R.string.khatunganj)));


        subAreaList.add(new SubArea("Khulshi", "Wireless mor", getString(R.string.wirelessmor)));
        subAreaList.add(new SubArea("Khulshi", "Khulsi shahid", getString(R.string.khulsi_shahid)));
        subAreaList.add(new SubArea("Khulshi", "Chattagram bandor nagori international university", getString(R.string.chattagram_bandor_nagori_international_university)));
        subAreaList.add(new SubArea("Khulshi", "Chittagong Government Women's College", getString(R.string.chittagong_government_women_college)));
        subAreaList.add(new SubArea("Khulshi", "Jautla Railway Station", getString(R.string.jautla_railway_station)));
        subAreaList.add(new SubArea("Khulshi", "Polytechnic Institute", getString(R.string.polytechnic_institute)));
        subAreaList.add(new SubArea("Khulshi", "Foyeslech", getString(R.string.foyeslech)));
        subAreaList.add(new SubArea("Khulshi", "Bangladesh Agricultural Research Centre", getString(R.string.bangladesh_agricultural_research_centre)));
        subAreaList.add(new SubArea("Khulshi", "BGMEA", getString(R.string.bgmea)));
        subAreaList.add(new SubArea("Khulshi", "Holy crescent bus stop", getString(R.string.holy_crescent_bus_stop)));

        subAreaList.add(new SubArea("Gosaildanga", "Gosaildanga", getString(R.string.gosaildanga)));

        subAreaList.add(new SubArea("Chowkbazar", "Wasa mor", getString(R.string.wasa_mor)));
        subAreaList.add(new SubArea("Chowkbazar", "Gani bekari mor", getString(R.string.gani_bekari_mor)));
        subAreaList.add(new SubArea("Chowkbazar", "Chawk bazar bus stop", getString(R.string.chawk_bazar_bus_stop)));
        subAreaList.add(new SubArea("Chowkbazar", "Chawk bazar super market", getString(R.string.chawk_bazar_super_market)));
        subAreaList.add(new SubArea("Chowkbazar", "Chattessori mor", getString(R.string.chattessori_mor)));
        subAreaList.add(new SubArea("Chowkbazar", "Jamal khan", getString(R.string.jamal_khan)));
        subAreaList.add(new SubArea("Chowkbazar", "Deb pahar", getString(R.string.deb_pahar)));
        subAreaList.add(new SubArea("Chowkbazar", "Perad moydan", getString(R.string.perad_moydan)));
        subAreaList.add(new SubArea("Chowkbazar", "Boddo mondir", getString(R.string.boddo_mondir)));

        subAreaList.add(new SubArea("Chattagrambondor", "Ekrampur Ispahani", getString(R.string.ekrampur_ispahani)));
        subAreaList.add(new SubArea("Chattagrambondor", "Kalgachiya", getString(R.string.kalgachiya)));
        subAreaList.add(new SubArea("Chattagrambondor", "Khaitkhali", getString(R.string.khaitkhali)));
        subAreaList.add(new SubArea("Chattagrambondor", "TinGau", getString(R.string.tin_gau)));
        subAreaList.add(new SubArea("Chattagrambondor", "Boro Nayabazar", getString(R.string.boro_nayabazar)));
        subAreaList.add(new SubArea("Chattagrambondor", "Bag Nayaghar", getString(R.string.bag_nayaghar)));
        subAreaList.add(new SubArea("Chattagrambondor", "Madanapura Masjid", getString(R.string.madanapura_masjid)));
        subAreaList.add(new SubArea("Chattagrambondor", "Madanpur Khal", getString(R.string.madanpur_khal)));
        subAreaList.add(new SubArea("Chattagrambondor", "Madhav Pasha", getString(R.string.madhav_pasha)));
        subAreaList.add(new SubArea("Chattagrambondor", "Mahmud Nagar", getString(R.string.mahmud_nagar)));
        subAreaList.add(new SubArea("Chattagrambondor", "Rasulbagh", getString(R.string.rasulbagh)));
        subAreaList.add(new SubArea("Chattagrambondor", "Langalbrand", getString(R.string.langalbrand)));
        subAreaList.add(new SubArea("Chattagrambondor", "Sonakanda", getString(R.string.sonakanda)));

        subAreaList.add(new SubArea("Chorpara", "Chor para", getString(R.string.chor_para)));

        subAreaList.add(new SubArea("Chorhalda", "Chor halda", getString(R.string.chor_halda)));

        subAreaList.add(new SubArea("Chadgao", "Kaptai rastar matha", getString(R.string.kaptai_rastar_matha)));
        subAreaList.add(new SubArea("Chadgao", "Kalurghat bus stop", getString(R.string.kalurghat_bus_stop)));
        subAreaList.add(new SubArea("Chadgao", "Chattagram betar kendro", getString(R.string.chattragram_betar_kendro)));
        subAreaList.add(new SubArea("Chadgao", "Chadgao abashik", getString(R.string.chadgao_abashik)));
        subAreaList.add(new SubArea("Chadgao", "Bohoddarhat", getString(R.string.bohoddar_hat)));
        subAreaList.add(new SubArea("Chadgao", "Bus terminal", getString(R.string.bus_terminal)));
        subAreaList.add(new SubArea("Chadgao", "Bahir signal", getString(R.string.bahir_signal)));
        subAreaList.add(new SubArea("Chadgao", "Moulvibazar", getString(R.string.moulvibazar)));
        subAreaList.add(new SubArea("Chadgao", "CNB", getString(R.string.cnb)));
        subAreaList.add(new SubArea("Chadgao", "Haji Saber Ahmed Timber Company Limited", getString(R.string.haji_saber_ahmed_timber)));
        subAreaList.add(new SubArea("Chadgao", "Hajera Taju degree college", getString(R.string.hajera_taju_degree_college)));

        subAreaList.add(new SubArea("ChowdhuryPara", "Chowdhury Para", getString(R.string.chowdhury_para)));


        subAreaList.add(new SubArea("GEMofficerscolony", "GEM officers colony", getString(R.string.gem_officers_colony)));
        subAreaList.add(new SubArea("Jhawtola", "Jhawtola", getString(R.string.jhawtola)));
        subAreaList.add(new SubArea("Tigerpassrailwaycolony", "Tigerpass railway colony", getString(R.string.tigerpass_railway_colony)));

        subAreaList.add(new SubArea("TSPcolony", "TSP colony", getString(R.string.tsp_colony)));
        subAreaList.add(new SubArea("Teribazar", "Teri bazar", getString(R.string.teri_bazar)));


        subAreaList.add(new SubArea("Doublemuring", "Chattagram bondor", getString(R.string.chattagram_bondor)));
        subAreaList.add(new SubArea("Doublemuring", "Double muring", getString(R.string.double_muring)));
        subAreaList.add(new SubArea("Doublemuring", "South agrabad", getString(R.string.south_agrabad)));
        subAreaList.add(new SubArea("Doublemuring", "Noya bazar pahartoli", getString(R.string.noyabazar_pahartoli)));
        subAreaList.add(new SubArea("Doublemuring", "Bou bazar", getString(R.string.bou_bazar)));
        subAreaList.add(new SubArea("Doublemuring", "Pahartoli", getString(R.string.pahartoli)));
        subAreaList.add(new SubArea("Doublemuring", "Bangladesh Bank", getString(R.string.bangladesh_bank_colony)));
        subAreaList.add(new SubArea("Doublemuring", "Bou bazar", getString(R.string.bou_bazar)));
        subAreaList.add(new SubArea("Doublemuring", "Mohuri paara", getString(R.string.mohuri_para)));
        subAreaList.add(new SubArea("Doublemuring", "Soray para", getString(R.string.soray_para)));

        subAreaList.add(new SubArea("Doijpara", "Doijpara", getString(R.string.doijpara)));
        subAreaList.add(new SubArea("Southpatenga", "South patenga", getString(R.string.south_patenga)));
        subAreaList.add(new SubArea("Southbondar", "South bondar", getString(R.string.south_bondor)));
        subAreaList.add(new SubArea("Dokkhinmoddhoholyshohor", "Dokkhin moddho holyshohor", getString(R.string.dokkhin_moddho_holyshohor)));
        subAreaList.add(new SubArea("Dampara", "Dampara", getString(R.string.dampara)));
        subAreaList.add(new SubArea("Deoyanghat", "Deoyan ghat", getString(R.string.deyan_ghat)));
        subAreaList.add(new SubArea("Dewanbazar", "Dewan bazar", getString(R.string.dewan_bazar)));
        subAreaList.add(new SubArea("Dhumpara", "Dhumpara", getString(R.string.dhumpara)));
        subAreaList.add(new SubArea("Northmiddleholishohor", "North middle holishohor", getString(R.string.north_middle_holishohor)));
        subAreaList.add(new SubArea("Nasirabad", "Nasirabad", getString(R.string.nasirabad)));
        subAreaList.add(new SubArea("Newmuring", "New muring", getString(R.string.new_muring)));
        subAreaList.add(new SubArea("Navyport", "Navy port", getString(R.string.navy_port)));
        subAreaList.add(new SubArea("Podmaabashikalaka", "Podma abashik alaka", getString(R.string.podma_abashik_alaka)));


        subAreaList.add(new SubArea("Paslaish", "Aturar dipu", getString(R.string.aturar_dipu)));
        subAreaList.add(new SubArea("Paslaish", "Amirbug R/A", getString(R.string.amirbug_r_a)));
        subAreaList.add(new SubArea("Paslaish", "Chattroseri", getString(R.string.chattroseri)));
        subAreaList.add(new SubArea("Paslaish", "GEC mor", getString(R.string.gec_mor)));
        subAreaList.add(new SubArea("Paslaish", "Pashlaish R/A", getString(R.string.pashlaish_ra)));
        subAreaList.add(new SubArea("Paslaish", "Peyara bagan", getString(R.string.payara_bagan)));
        subAreaList.add(new SubArea("Paslaish", "Bagmoniram", getString(R.string.bagmoniram)));
        subAreaList.add(new SubArea("Paslaish", "KhatMufijur rahman abashik alakaungonj", getString(R.string.mufijur_rahman_abashik_alaka)));
        subAreaList.add(new SubArea("Paslaish", "Murad pur", getString(R.string.muradpur)));
        subAreaList.add(new SubArea("Paslaish", "Medical staff quarter", getString(R.string.medical_staff_quarter)));
        subAreaList.add(new SubArea("Paslaish", "Mehedi bug", getString(R.string.mehedibug)));
        subAreaList.add(new SubArea("Paslaish", "Shulkobohor", getString(R.string.shulkobohor)));
        subAreaList.add(new SubArea("Paslaish", "Sholoshohor", getString(R.string.sholoshohor)));
        subAreaList.add(new SubArea("Paslaish", "Sholoshohor railway station", getString(R.string.sholoshorer_railway_station)));
        subAreaList.add(new SubArea("Paslaish", "Hamjarbag", getString(R.string.hamjarbug)));
        subAreaList.add(new SubArea("Paslaish", "2 no. gate", getString(R.string._2_n0_gate)));

        subAreaList.add(new SubArea("Pathantuli", "Pathantuli", getString(R.string.pathantuli)));


        subAreaList.add(new SubArea("Potenga", "South port", getString(R.string.south_port)));
        subAreaList.add(new SubArea("Potenga", "Najira para", getString(R.string.najirapara)));
        subAreaList.add(new SubArea("Potenga", "Navy colony", getString(R.string.navy_colony)));
        subAreaList.add(new SubArea("Potenga", "Patenga beach", getString(R.string.patenga_beatch)));
        subAreaList.add(new SubArea("Potenga", "Porapara", getString(R.string.porapara)));
        subAreaList.add(new SubArea("Potenga", "Bangladesh navy golf club", getString(R.string.banglladesh_navy_golf_club)));
        subAreaList.add(new SubArea("Potenga", "Muslimabad", getString(R.string.muslimabad)));
        subAreaList.add(new SubArea("Potenga", "Steel industries", getString(R.string.steel_industries)));


        subAreaList.add(new SubArea("Patharghata", "Patharghata", getString(R.string.patharghata)));

        subAreaList.add(new SubArea("Pahartali", "North katrali", getString(R.string.north_katrali)));
        subAreaList.add(new SubArea("Pahartali", "Koibolo dam railway station", getString(R.string.koibolo_dam_railway_station)));
        subAreaList.add(new SubArea("Pahartali", "Cricket stadium railway station", getString(R.string.cricket_stadium_railway_station)));
        subAreaList.add(new SubArea("Pahartali", "South katrali", getString(R.string.south_katrail)));
        subAreaList.add(new SubArea("Pahartali", "Pahartoli railway station", getString(R.string.pahartoli_railway_station)));
        subAreaList.add(new SubArea("Pahartali", "Firoz shah uponibesh", getString(R.string.firoz_shah_railway_station)));
        subAreaList.add(new SubArea("Pahartali", "Bishorzo para", getString(R.string.bishorzopara)));
        subAreaList.add(new SubArea("Pahartali", "Samoli abashik alaka", getString(R.string.samoli_abashik_alaka)));


        subAreaList.add(new SubArea("Purbonimtala", "Purbo nimtala", getString(R.string.purbo_nimtala)));

        subAreaList.add(new SubArea("Purbomadarbari", "Purbo madar bari", getString(R.string.purbo_madar_bari)));

        subAreaList.add(new SubArea("Bahaddarhat", "Bahaddarhat", getString(R.string.bahaddarhat)));
        subAreaList.add(new SubArea("Bangladeshbankcolony", "Bangladesh bank colony", getString(R.string.bangladesh_bank_colony)));

        subAreaList.add(new SubArea("Bakolia", "Kalmia Bazar", getString(R.string.kalmia_bazar)));
        subAreaList.add(new SubArea("Bakolia", "Khatunganj", getString(R.string.khatunganj)));
        subAreaList.add(new SubArea("Bakolia", "Goni Bakeri More", getString(R.string.goni_bakerimore)));
        subAreaList.add(new SubArea("Bakolia", "Dewan Bazar", getString(R.string.dewan_bazar)));
        subAreaList.add(new SubArea("Bakolia", "Pathorghata", getString(R.string.pathorghata)));
        subAreaList.add(new SubArea("Bakolia", "Bakshirhat", getString(R.string.bakshirhat)));
        subAreaList.add(new SubArea("Bakolia", "Maizpara", getString(R.string.maizpara)));
        subAreaList.add(new SubArea("Bakolia", "Rahattorpul", getString(R.string.rahattorpul)));

        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.oxygen_more)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.amin_jut_mile)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.arefin_nagor)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.ali_nagor)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.chittagong_cant_public_college)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.textile_gate)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.nobi_nagor)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.nasirabad)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.poly_technical)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.bangladesh_forest_research_institute_gate)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.bayazid_bostami)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.barma_coloni)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.rahaman_nagor)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.rawfabad)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.sher_shah_coloni)));
        subAreaList.add(new SubArea("Bayazidbostami", "Bayazid bostami", getString(R.string.hamjarbag)));

        subAreaList.add(new SubArea("Mansurabad", "Mansurabad", getString(R.string.mansurabad)));
        subAreaList.add(new SubArea("Mansurabad", "Shafi Motors Limited", getString(R.string.shafi_motors_limited)));

        subAreaList.add(new SubArea("Maijpara", "Maijpara", getString(R.string.maizpara)));
        subAreaList.add(new SubArea("Rangiparabankcolony", "Rangipara bank colony", getString(R.string.rangipara_bank_colony)));
        subAreaList.add(new SubArea("Laldairchar", "Laldair char", getString(R.string.laldair_char)));

        subAreaList.add(new SubArea("Shadorghat", "Abhayamitra", getString(R.string.abhayamitra)));
        subAreaList.add(new SubArea("Shadorghat", "Karnaphuli Dockyard", getString(R.string.karnaphuli_dockyard)));
        subAreaList.add(new SubArea("Shadorghat", "Kamal Gate Bazar", getString(R.string.kamal_gate_bazar)));
        subAreaList.add(new SubArea("Shadorghat", "Choktai Khal", getString(R.string.choktai_khal)));
        subAreaList.add(new SubArea("Shadorghat", "Tin pooler matha", getString(R.string.tin_pool_matha)));
        subAreaList.add(new SubArea("Shadorghat", "Noton Bazar", getString(R.string.noton_bazar)));
        subAreaList.add(new SubArea("Shadorghat", "Bakshirhat", getString(R.string.baksirhat)));
        subAreaList.add(new SubArea("Shadorghat", "Motherbari Railway Pump Station", getString(R.string.motherbari_railway_pump_station)));
        subAreaList.add(new SubArea("Shadorghat", "Mia Khan Bridge", getString(R.string.mia_khan_setu)));
        subAreaList.add(new SubArea("Shadorghat", "Riaz Uddin Bazar", getString(R.string.riaz_uddin_bazar)));
        subAreaList.add(new SubArea("Shadorghat", "Laldigi more", getString(R.string.laldigi_more)));
        subAreaList.add(new SubArea("Shadorghat", "Sadarghat Jeti", getString(R.string.sadarghat_jt)));
        subAreaList.add(new SubArea("Shadorghat", "Cinema Place", getString(R.string.cinema_place)));

        subAreaList.add(new SubArea("Sondippara", "Sondip para", getString(R.string.sondip_para)));
        subAreaList.add(new SubArea("Southagrabad", "KhatunSouth agrabadgonj", getString(R.string.south_agrabad)));
        subAreaList.add(new SubArea("CGScolony", "CGS colony", getString(R.string.cgs_colony)));

        subAreaList.add(new SubArea("Hali_shohor", "KNT Logistics Limited", getString(R.string.knt_logistics_limited)));
        subAreaList.add(new SubArea("Hali_shohor", "Chittagong Container Terminal", getString(R.string.chittagong_container_terminal)));
        subAreaList.add(new SubArea("Hali_shohor", "Chittagong Container Transportation Co. Ltd", getString(R.string.chittagong_container_tran_co_ltd)));
        subAreaList.add(new SubArea("Hali_shohor", "Chittagong Bondor dharak", getString(R.string.chittagong_bondor_dharak)));
        subAreaList.add(new SubArea("Hali_shohor", "Daksin Halishahar", getString(R.string.daksin_halishahar)));
        subAreaList.add(new SubArea("Hali_shohor", "Noton sidebar", getString(R.string.noton_sidebar)));
        subAreaList.add(new SubArea("Hali_shohor", "Newmooring Container Terminal", getString(R.string.newmooring_container_terminal)));
        subAreaList.add(new SubArea("Hali_shohor", "Nau Bondor", getString(R.string.nau_bondor)));
        subAreaList.add(new SubArea("Hali_shohor", "Bondor new mooring", getString(R.string.bondor_new_mooring)));
        subAreaList.add(new SubArea("Hali_shohor", "Bondor link road", getString(R.string.bondor_link_road)));
        subAreaList.add(new SubArea("Hali_shohor", "Munshiipara", getString(R.string.munshiipara)));
        subAreaList.add(new SubArea("Hali_shohor", "Labor Colony", getString(R.string.labor_colony)));
        subAreaList.add(new SubArea("Hali_shohor", "Halishahar Housing Society", getString(R.string.halishahar_housing_society)));

        subAreaList.add(new SubArea("Halishohormunshipara", "Hali shohor munshipara", getString(R.string.hali_shohor_munshipara)));
        subAreaList.add(new SubArea("Halishohorsenanibash", "Hali shohor senanibash", getString(R.string.hali_shohor_senanibash)));
        subAreaList.add(new SubArea("Hosenahmedpara", "Hosen ahmedpara", getString(R.string.hosen_ahmedpara)));


        subAreaList.add(new SubArea("Faridpur", "Alfadanga", getString(R.string.alfadanga)));
        subAreaList.add(new SubArea("Faridpur", "Vanga", getString(R.string.vanga)));
        subAreaList.add(new SubArea("Faridpur", "Boyalmari", getString(R.string.boyalmari)));
        subAreaList.add(new SubArea("Faridpur", "Chorvodroson", getString(R.string.chorvodroson)));
        subAreaList.add(new SubArea("Faridpur", "Faridpur sadar", getString(R.string.faridpur_sadar)));
        subAreaList.add(new SubArea("Faridpur", "Madhukhali", getString(R.string.madhukhali)));
        subAreaList.add(new SubArea("Faridpur", "Nagarkanda", getString(R.string.nagarkanda)));
        subAreaList.add(new SubArea("Faridpur", "Sadarpur", getString(R.string.sadarpur)));
        subAreaList.add(new SubArea("Faridpur", "Saltha", getString(R.string.saltha)));


        subAreaList.add(new SubArea("Kishorgonj", "Ostogram", getString(R.string.ostogram)));
        subAreaList.add(new SubArea("Kishorgonj", "Bajitpur", getString(R.string.bajitpur)));
        subAreaList.add(new SubArea("Kishorgonj", "Karimganj", getString(R.string.karimganj)));
        subAreaList.add(new SubArea("Kishorgonj", "Bhairab", getString(R.string.bhairab)));
        subAreaList.add(new SubArea("Kishorgonj", "Hosenpur", getString(R.string.hosenpur)));
        subAreaList.add(new SubArea("Kishorgonj", "Itna", getString(R.string.itna)));
        subAreaList.add(new SubArea("Kishorgonj", "Katiadi", getString(R.string.katiadi)));
        subAreaList.add(new SubArea("Kishorgonj", "Kishoreganj Sadar", getString(R.string.kishoreganj_sadar)));
        subAreaList.add(new SubArea("Kishorgonj", "Kuliyachor", getString(R.string.kuliyachor)));
        subAreaList.add(new SubArea("Kishorgonj", "Mithamoin", getString(R.string.mithamoin)));
        subAreaList.add(new SubArea("Kishorgonj", "Nikoli", getString(R.string.nikoli)));
        subAreaList.add(new SubArea("Kishorgonj", "Pakundia", getString(R.string.pakundia)));
        subAreaList.add(new SubArea("Kishorgonj", "Tarail", getString(R.string.tarail)));


        subAreaList.add(new SubArea("Rajbari", "Baliakandi", getString(R.string.baliakandi)));
        subAreaList.add(new SubArea("Rajbari", "Goyalondo", getString(R.string.goyalondo)));
        subAreaList.add(new SubArea("Rajbari", "Kalukhali", getString(R.string.kalukhali)));
        subAreaList.add(new SubArea("Rajbari", "Pangsha", getString(R.string.pangsha)));
        subAreaList.add(new SubArea("Rajbari", "Rajbari Sadar", getString(R.string.rajbari_sadar)));


        subAreaList.add(new SubArea("Tangail", "Basail", getString(R.string.basail)));
        subAreaList.add(new SubArea("Tangail", "Vuyapur", getString(R.string.vuyapur)));
        subAreaList.add(new SubArea("Tangail", "Delduar", getString(R.string.delduar)));
        subAreaList.add(new SubArea("Tangail", "Dhanbari", getString(R.string.dhanbari)));
        subAreaList.add(new SubArea("Tangail", "Ghatail", getString(R.string.ghatail)));
        subAreaList.add(new SubArea("Tangail", "Gopalpur", getString(R.string.gopalpur)));
        subAreaList.add(new SubArea("Tangail", "Kalihati", getString(R.string.kalihati)));
        subAreaList.add(new SubArea("Tangail", "Madhupur", getString(R.string.madhupur)));
        subAreaList.add(new SubArea("Tangail", "Mirzapur", getString(R.string.mirzapur)));
        subAreaList.add(new SubArea("Tangail", "Nagarpur", getString(R.string.nagarpur)));
        subAreaList.add(new SubArea("Tangail", "Sakhipur", getString(R.string.sakhipur)));
        subAreaList.add(new SubArea("Tangail", "Tangail Sadar", getString(R.string.tangail_sadar)));


        subAreaList.add(new SubArea("Narsingdi", "Belabo", getString(R.string.belabo)));
        subAreaList.add(new SubArea("Narsingdi", "Monohardi", getString(R.string.monohardi)));
        subAreaList.add(new SubArea("Narsingdi", "Narsingdi sadar", getString(R.string.narsingdi_sadar)));
        subAreaList.add(new SubArea("Narsingdi", "Polash", getString(R.string.polash)));
        subAreaList.add(new SubArea("Narsingdi", "Raipura", getString(R.string.raipura)));
        subAreaList.add(new SubArea("Narsingdi", "Shibpur", getString(R.string.shibpur)));


        subAreaList.add(new SubArea("Shariatpur", "Vedorganj", getString(R.string.vedorganj)));
        subAreaList.add(new SubArea("Shariatpur", "Damuda", getString(R.string.demuda)));
        subAreaList.add(new SubArea("Shariatpur", "Gosairhat", getString(R.string.gosairhat)));
        subAreaList.add(new SubArea("Shariatpur", "Noriya", getString(R.string.noriya)));
        subAreaList.add(new SubArea("Shariatpur", "Shariatpur Sadar", getString(R.string.shariatpur_sadar)));
        subAreaList.add(new SubArea("Shariatpur", "Jajira", getString(R.string.jajira)));


        subAreaList.add(new SubArea("Dhaka", "Dhamrai", getString(R.string.dhamrai)));
        subAreaList.add(new SubArea("Dhaka", "Dohar", getString(R.string.dohar)));
        subAreaList.add(new SubArea("Dhaka", "Keraniganj", getString(R.string.keraniganj)));
        subAreaList.add(new SubArea("Dhaka", "Nobabganj", getString(R.string.nobabganj)));
        subAreaList.add(new SubArea("Dhaka", "Savar", getString(R.string.savar)));

        subAreaList.add(new SubArea("Manikgonj", "Doulatpur", getString(R.string.doulotpur)));
        subAreaList.add(new SubArea("Manikgonj", "Ghior", getString(R.string.ghior)));
        subAreaList.add(new SubArea("Manikgonj", "Harirampur", getString(R.string.harirampur)));
        subAreaList.add(new SubArea("Manikgonj", "Manikganj sadar", getString(R.string.manikganj_sadar)));
        subAreaList.add(new SubArea("Manikgonj", "Saturia", getString(R.string.saturia)));
        subAreaList.add(new SubArea("Manikgonj", "Shibaloy", getString(R.string.shibloy)));
        subAreaList.add(new SubArea("Manikgonj", "Singair", getString(R.string.singair)));


        subAreaList.add(new SubArea("Munshigonj", "Gozaria", getString(R.string.gozaria)));
        subAreaList.add(new SubArea("Munshigonj", "Louhajong", getString(R.string.louhajong)));
        subAreaList.add(new SubArea("Munshigonj", "Munshiganj sadar", getString(R.string.munshiganj_sadar)));
        subAreaList.add(new SubArea("Munshigonj", "Srinagar", getString(R.string.srinagar)));
        subAreaList.add(new SubArea("Munshigonj", "Sirajdikhan", getString(R.string.sirajdikhan)));
        subAreaList.add(new SubArea("Munshigonj", "Tongibari", getString(R.string.tongibari)));


        subAreaList.add(new SubArea("Gopalgonj", "Gopalganj sadar", getString(R.string.gopalganj_sadar)));
        subAreaList.add(new SubArea("Gopalgonj", "Kashiyani", getString(R.string.kashiyani)));
        subAreaList.add(new SubArea("Gopalgonj", "Kotalipara", getString(R.string.kotalipara)));
        subAreaList.add(new SubArea("Gopalgonj", "Muksudpur", getString(R.string.muksudpur)));
        subAreaList.add(new SubArea("Gopalgonj", "Tungipara", getString(R.string.tungipara)));


        subAreaList.add(new SubArea("Madaripur", "Kalkini", getString(R.string.kalkini)));
        subAreaList.add(new SubArea("Madaripur", "Dasar", getString(R.string.dasar)));
        subAreaList.add(new SubArea("Madaripur", "Madaripur sadar", getString(R.string.madaripur_sadar)));
        subAreaList.add(new SubArea("Madaripur", "Rajoub", getString(R.string.rajoiub)));
        subAreaList.add(new SubArea("Madaripur", "Shibchar", getString(R.string.shibchar)));


        subAreaList.add(new SubArea("NarayanGanj", "Araihazar", getString(R.string.araihazar)));
        subAreaList.add(new SubArea("NarayanGanj", "Bandar", getString(R.string.bandar)));
        subAreaList.add(new SubArea("NarayanGanj", "Narayanganj sadar", getString(R.string.narayanganj_sadar)));
        subAreaList.add(new SubArea("NarayanGanj", "Narayanganj city", getString(R.string.narayanganj_city)));
        subAreaList.add(new SubArea("NarayanGanj", "Rupganj", getString(R.string.rupganj)));
        subAreaList.add(new SubArea("NarayanGanj", "Sonargaon", getString(R.string.sonargoan)));
        subAreaList.add(new SubArea("NarayanGanj", "Fatullah", getString(R.string.fatullah)));


        subAreaList.add(new SubArea("Gazipur", "Kaliganj", getString(R.string.kaliganj)));
        subAreaList.add(new SubArea("Gazipur", "Kaliakoir", getString(R.string.kaliakoir)));
        subAreaList.add(new SubArea("Gazipur", "Kapasia", getString(R.string.kapasia)));
        subAreaList.add(new SubArea("Gazipur", "Basan", getString(R.string.basan)));
        subAreaList.add(new SubArea("Gazipur", "Gazipur sadar", getString(R.string.gazipur_sadar)));
        subAreaList.add(new SubArea("Gazipur", "Gazipur city corporation", getString(R.string.gazipur_city_corporation)));
        subAreaList.add(new SubArea("Gazipur", "Sripur", getString(R.string.sripur)));
        subAreaList.add(new SubArea("Gazipur", "Kayaloti", getString(R.string.kayaloti)));
        subAreaList.add(new SubArea("Gazipur", "Konabari", getString(R.string.konabari)));
        subAreaList.add(new SubArea("Gazipur", "Gasa", getString(R.string.gasa)));
        subAreaList.add(new SubArea("Gazipur", "Kashimpur", getString(R.string.kashimpur)));


        subAreaList.add(new SubArea("Brahmanbaria", "Bancharampur", getString(R.string.bancharampur)));
        subAreaList.add(new SubArea("Brahmanbaria", "Bijoynagar", getString(R.string.bijoynagar)));
        subAreaList.add(new SubArea("Brahmanbaria", "Akhaura", getString(R.string.akhaura)));
        subAreaList.add(new SubArea("Brahmanbaria", "Ashugonj", getString(R.string.ashuganj)));
        subAreaList.add(new SubArea("Brahmanbaria", "Kosba", getString(R.string.kosba)));
        subAreaList.add(new SubArea("Brahmanbaria", "Nobinogor", getString(R.string.nabinagar)));
        subAreaList.add(new SubArea("Brahmanbaria", "Nasirnogor", getString(R.string.nasirnagar)));
        subAreaList.add(new SubArea("Brahmanbaria", "Brahmanbaria sadar", getString(R.string.brahmanbaria_sadar)));
        subAreaList.add(new SubArea("Brahmanbaria", "Sarail", getString(R.string.sarail)));


        subAreaList.add(new SubArea("Bandarban", "Alikodom", getString(R.string.alikodom)));
        subAreaList.add(new SubArea("Bandarban", "Thanchi", getString(R.string.thanchi)));
        subAreaList.add(new SubArea("Bandarban", "Naikkhongchori", getString(R.string.naikkhonchori)));
        subAreaList.add(new SubArea("Bandarban", "Ruma", getString(R.string.ruma)));
        subAreaList.add(new SubArea("Bandarban", "Bandarban sadar", getString(R.string.bandarban_sadar)));
        subAreaList.add(new SubArea("Bandarban", "Rongchori", getString(R.string.rongchori)));
        subAreaList.add(new SubArea("Bandarban", "Lama", getString(R.string.lama)));


        subAreaList.add(new SubArea("Chittagong", "Anoyara", getString(R.string.anoyara)));
        subAreaList.add(new SubArea("Chittagong", "Chondonaish", getString(R.string.chondonaish)));
        subAreaList.add(new SubArea("Chittagong", "Bashkhali", getString(R.string.bashkhali)));
        subAreaList.add(new SubArea("Chittagong", "Boalkhali", getString(R.string.boalkhali)));
        subAreaList.add(new SubArea("Chittagong", "Mirsarai", getString(R.string.mirsarai)));
        subAreaList.add(new SubArea("Chittagong", "Sondip", getString(R.string.sondip)));
        subAreaList.add(new SubArea("Chittagong", "Satkania", getString(R.string.satkania)));
        subAreaList.add(new SubArea("Chittagong", "Hathazari", getString(R.string.hathazari)));
        subAreaList.add(new SubArea("Chittagong", "Kornofuli", getString(R.string.kornofuli)));
        subAreaList.add(new SubArea("Chittagong", "Potiya", getString(R.string.potiya)));
        subAreaList.add(new SubArea("Chittagong", "Fotikchori", getString(R.string.fotikchori)));
        subAreaList.add(new SubArea("Chittagong", "Roujan", getString(R.string.roujan)));
        subAreaList.add(new SubArea("Chittagong", "Rangunia", getString(R.string.rangunia)));
        subAreaList.add(new SubArea("Chittagong", "Lohagara", getString(R.string.hohagara)));
        subAreaList.add(new SubArea("Chittagong", "Sitakunda", getString(R.string.sitakunda)));


        subAreaList.add(new SubArea("Rangamati", "Kaptai", getString(R.string.kaptai)));
        subAreaList.add(new SubArea("Rangamati", "Kaukhali", getString(R.string.kaukhali)));
        subAreaList.add(new SubArea("Rangamati", "Jurachori", getString(R.string.jurachri)));
        subAreaList.add(new SubArea("Rangamati", "Naniarchar", getString(R.string.naniarchar)));
        subAreaList.add(new SubArea("Rangamati", "Borkol", getString(R.string.borkol)));
        subAreaList.add(new SubArea("Rangamati", "Bagaichori", getString(R.string.bagaichori)));
        subAreaList.add(new SubArea("Rangamati", "Bilaichori", getString(R.string.bilaichori)));
        subAreaList.add(new SubArea("Rangamati", "Rangamati sadar", getString(R.string.rangamati_sadar)));
        subAreaList.add(new SubArea("Rangamati", "Rajstoli", getString(R.string.rajstoli)));
        subAreaList.add(new SubArea("Rangamati", "Longgodu", getString(R.string.longodu)));


        subAreaList.add(new SubArea("Comilla", "Nangalkot", getString(R.string.nangalkot)));
        subAreaList.add(new SubArea("Comilla", "Burichong", getString(R.string.burichong)));
        subAreaList.add(new SubArea("Comilla", "Muradnagar", getString(R.string.muradnagar)));
        subAreaList.add(new SubArea("Comilla", "Comilla city", getString(R.string.comilla_city)));
        subAreaList.add(new SubArea("Comilla", "Comilla sadar", getString(R.string.comilla_sadar)));
        subAreaList.add(new SubArea("Comilla", "Chandina", getString(R.string.chandina)));
        subAreaList.add(new SubArea("Comilla", "Choddogram", getString(R.string.choddogram)));
        subAreaList.add(new SubArea("Comilla", "Titas", getString(R.string.titas)));
        subAreaList.add(new SubArea("Comilla", "Debidar", getString(R.string.debidar)));
        subAreaList.add(new SubArea("Comilla", "Daudkandi", getString(R.string.daudkandi)));
        subAreaList.add(new SubArea("Comilla", "Borura", getString(R.string.borura)));
        subAreaList.add(new SubArea("Comilla", "Brahmanpara", getString(R.string.brahmanpara)));
        subAreaList.add(new SubArea("Comilla", "Monohorgonj", getString(R.string.monohorganj)));
        subAreaList.add(new SubArea("Comilla", "Megna", getString(R.string.megna)));
        subAreaList.add(new SubArea("Comilla", "Laksham", getString(R.string.laksam)));
        subAreaList.add(new SubArea("Comilla", "Lalmai", getString(R.string.lalmai)));
        subAreaList.add(new SubArea("Comilla", "Sadar dokkhin", getString(R.string.sadar_dokkhin)));
        subAreaList.add(new SubArea("Comilla", "Homna", getString(R.string.homna)));


        subAreaList.add(new SubArea("Noakhali", "Kabirhat", getString(R.string.kabirhat)));
        subAreaList.add(new SubArea("Noakhali", "Kompanigonj", getString(R.string.kompaniganj)));
        subAreaList.add(new SubArea("Noakhali", "Chatkhil", getString(R.string.chatkhil)));
        subAreaList.add(new SubArea("Noakhali", "Noakhali sadar", getString(R.string.noakhali_sadar)));
        subAreaList.add(new SubArea("Noakhali", "Begumgonj", getString(R.string.begumganj)));
        subAreaList.add(new SubArea("Noakhali", "Subornochor", getString(R.string.subornochor)));
        subAreaList.add(new SubArea("Noakhali", "Senbag", getString(R.string.senbug)));
        subAreaList.add(new SubArea("Noakhali", "Sonaimuri", getString(R.string.sonaimuri)));
        subAreaList.add(new SubArea("Noakhali", "Hatiya", getString(R.string.hatiya)));


        subAreaList.add(new SubArea("CoxsBazar", "Ukhiya", getString(R.string.ukhiya)));
        subAreaList.add(new SubArea("CoxsBazar", "Kutubdia", getString(R.string.kutubdia)));
        subAreaList.add(new SubArea("CoxsBazar", "Cox's Bazar sadar", getString(R.string.coxsbazar_sadar)));
        subAreaList.add(new SubArea("CoxsBazar", "Chokoria", getString(R.string.chokoria)));
        subAreaList.add(new SubArea("CoxsBazar", "Teknaf", getString(R.string.teknaf)));
        subAreaList.add(new SubArea("CoxsBazar", "Pekuya", getString(R.string.pekuya)));
        subAreaList.add(new SubArea("CoxsBazar", "Moheshkhali", getString(R.string.moheshkhali)));
        subAreaList.add(new SubArea("CoxsBazar", "Ramu", getString(R.string.ramu)));


        subAreaList.add(new SubArea("Chandpur", "Kochuya", getString(R.string.kochuya)));
        subAreaList.add(new SubArea("Chandpur", "Motlob dokkhain", getString(R.string.motlob_dokkhin)));
        subAreaList.add(new SubArea("Chandpur", "Chandpur sadar", getString(R.string.chandpur_sadar)));
        subAreaList.add(new SubArea("Chandpur", "Faridgonj", getString(R.string.faridganj)));
        subAreaList.add(new SubArea("Chandpur", "Motlob uttar", getString(R.string.motlob_sadar)));
        subAreaList.add(new SubArea("Chandpur", "Shahrasti", getString(R.string.shahrasti)));
        subAreaList.add(new SubArea("Chandpur", "Haimchor", getString(R.string.hhaimchor)));
        subAreaList.add(new SubArea("Chandpur", "Hajigonj", getString(R.string.hajiganj)));


        subAreaList.add(new SubArea("Feni", "Dagonbhuiyan", getString(R.string.dagunbhuiyan)));
        subAreaList.add(new SubArea("Feni", "Chagla naiya", getString(R.string.changla_naiya)));
        subAreaList.add(new SubArea("Feni", "Porshuram", getString(R.string.porshuram)));
        subAreaList.add(new SubArea("Feni", "Fulgazi", getString(R.string.fulgazi)));
        subAreaList.add(new SubArea("Feni", "Feni sadar", getString(R.string.feni_sadar)));
        subAreaList.add(new SubArea("Feni", "Sonagazi", getString(R.string.sonagazi)));


        subAreaList.add(new SubArea("Khagrasori", "Guimara", getString(R.string.guimara)));
        subAreaList.add(new SubArea("Khagrasori", "Matiranga", getString(R.string.matiranga)));
        subAreaList.add(new SubArea("Khagrasori", "Manikchari", getString(R.string.manikchari)));
        subAreaList.add(new SubArea("Khagrasori", "Ramgarh", getString(R.string.ramgarh)));
        subAreaList.add(new SubArea("Khagrasori", "Khagrachari sadar", getString(R.string.khagrachari_sadar)));
        subAreaList.add(new SubArea("Khagrasori", "Dighinala", getString(R.string.dighinala)));
        subAreaList.add(new SubArea("Khagrasori", "Panchori", getString(R.string.panchori)));
        subAreaList.add(new SubArea("Khagrasori", "Mohalchori", getString(R.string.mohalchori)));
        subAreaList.add(new SubArea("Khagrasori", "Lokkhichori", getString(R.string.lokkhichori)));


        subAreaList.add(new SubArea("Lokkhipur", "Komol Nogor", getString(R.string.komol_nagar)));
        subAreaList.add(new SubArea("Lokkhipur", "Ramgoti", getString(R.string.ramgoti)));
        subAreaList.add(new SubArea("Lokkhipur", "Raipur", getString(R.string.raipur)));
        subAreaList.add(new SubArea("Lokkhipur", "Ramgonj", getString(R.string.ramganj)));
        subAreaList.add(new SubArea("Lokkhipur", "Lakshmipur sadar", getString(R.string.laksmipur_sadar)));


        subAreaList.add(new SubArea("Barisal", "Agoilojhara", getString(R.string.agoilojhara)));
        subAreaList.add(new SubArea("Barisal", "Babuganj", getString(R.string.babuganj)));
        subAreaList.add(new SubArea("Barisal", "Bakerganj", getString(R.string.bakerganj)));
        subAreaList.add(new SubArea("Barisal", "Banaripara", getString(R.string.banaripara)));
        subAreaList.add(new SubArea("Barisal", "Barisal sadar", getString(R.string.barishal_sadar)));
        subAreaList.add(new SubArea("Barisal", "Gournadi", getString(R.string.gournadi)));
        subAreaList.add(new SubArea("Barisal", "Mehendiganj", getString(R.string.mehendiganj)));
        subAreaList.add(new SubArea("Barisal", "Muladi", getString(R.string.muladi)));
        subAreaList.add(new SubArea("Barisal", "Hijla", getString(R.string.hijla)));
        subAreaList.add(new SubArea("Barisal", "Ujirpur", getString(R.string.ujirpur)));
        subAreaList.add(new SubArea("Barisal", "Barisal city", getString(R.string.barisal_city)));


        subAreaList.add(new SubArea("Barguna", "Amtali", getString(R.string.amtali)));
        subAreaList.add(new SubArea("Barguna", "Bamna", getString(R.string.bamna)));
        subAreaList.add(new SubArea("Barguna", "Barguna sadar", getString(R.string.barguna_sadar)));
        subAreaList.add(new SubArea("Barguna", "Betagi", getString(R.string.betagi)));
        subAreaList.add(new SubArea("Barguna", "Patharghata", getString(R.string.patharghata)));
        subAreaList.add(new SubArea("Barguna", "Taltoli", getString(R.string.taltoli)));


        subAreaList.add(new SubArea("Patuakhali", "Baufal", getString(R.string.baufal)));
        subAreaList.add(new SubArea("Patuakhali", "Doshmina", getString(R.string.doshmina)));
        subAreaList.add(new SubArea("Patuakhali", "Dumki", getString(R.string.dumki)));
        subAreaList.add(new SubArea("Patuakhali", "Galachipa", getString(R.string.galachipa)));
        subAreaList.add(new SubArea("Patuakhali", "Kalapara", getString(R.string.kalapara)));
        subAreaList.add(new SubArea("Patuakhali", "Mirzaganj", getString(R.string.mirzaganj)));
        subAreaList.add(new SubArea("Patuakhali", "Patuakhali sadar", getString(R.string.patuakhali_sadar)));
        subAreaList.add(new SubArea("Patuakhali", "Rangabali", getString(R.string.rangabali)));


        subAreaList.add(new SubArea("Pirojpur", "Vandariya", getString(R.string.vandariya)));
        subAreaList.add(new SubArea("Pirojpur", "Kaukhali", getString(R.string.kaukhali)));
        subAreaList.add(new SubArea("Pirojpur", "Motbariya", getString(R.string.motbariya)));
        subAreaList.add(new SubArea("Pirojpur", "Najirpur", getString(R.string.najirpur)));
        subAreaList.add(new SubArea("Pirojpur", "Nesarabad", getString(R.string.nesarabad)));
        subAreaList.add(new SubArea("Pirojpur", "Pirojpur Sadar", getString(R.string.pirojpur_sadar)));
        subAreaList.add(new SubArea("Pirojpur", "Jiyanagar", getString(R.string.jiyanagar)));


        subAreaList.add(new SubArea("Bhola", "Bhola sadar", getString(R.string.bhola_sadar)));
        subAreaList.add(new SubArea("Bhola", "Borhanuddin", getString(R.string.borhanuddn)));
        subAreaList.add(new SubArea("Bhola", "Charfashion", getString(R.string.charfashion)));
        subAreaList.add(new SubArea("Bhola", "Doulathkhan", getString(R.string.doulatkhan)));
        subAreaList.add(new SubArea("Bhola", "Lalmohan", getString(R.string.lalmohan)));
        subAreaList.add(new SubArea("Bhola", "Monpura", getString(R.string.monpura)));
        subAreaList.add(new SubArea("Bhola", "Tajumuddin", getString(R.string.tajumuddin)));


        subAreaList.add(new SubArea("Jhalokati", "Jhalokati sadar", getString(R.string.jhalokati_sadar)));
        subAreaList.add(new SubArea("Jhalokati", "Kathaliya", getString(R.string.kathaliya)));
        subAreaList.add(new SubArea("Jhalokati", "Nolsity", getString(R.string.nolsity)));
        subAreaList.add(new SubArea("Jhalokati", "Rajapur", getString(R.string.rajapur)));


        subAreaList.add(new SubArea("Jessore", "Avoynagar", getString(R.string.avoynagar)));
        subAreaList.add(new SubArea("Jessore", "Bagharpara", getString(R.string.bagharpara)));
        subAreaList.add(new SubArea("Jessore", "Chougasa", getString(R.string.chougasa)));
        subAreaList.add(new SubArea("Jessore", "Jessore sadar", getString(R.string.jessore_sadar)));
        subAreaList.add(new SubArea("Jessore", "Jhikargacha", getString(R.string.jhikargacha)));
        subAreaList.add(new SubArea("Jessore", "Keshabpur", getString(R.string.keshabpur)));
        subAreaList.add(new SubArea("Jessore", "Monirampur", getString(R.string.monirampur)));
        subAreaList.add(new SubArea("Jessore", "Sharsha", getString(R.string.sharsha)));


        subAreaList.add(new SubArea("Chuadanga", "Alamdanga", getString(R.string.alamdanga)));
        subAreaList.add(new SubArea("Chuadanga", "Chuadanga sadar", getString(R.string.chuadanga_sadar)));
        subAreaList.add(new SubArea("Chuadanga", "Damurhuda", getString(R.string.damurhuda)));
        subAreaList.add(new SubArea("Chuadanga", "Jibonnagar", getString(R.string.jibonnagar)));

        subAreaList.add(new SubArea("Satkhira", "Ashasuni", getString(R.string.ashasuni)));
        subAreaList.add(new SubArea("Satkhira", "Debhata", getString(R.string.debhata)));
        subAreaList.add(new SubArea("Satkhira", "Kolaroya", getString(R.string.kolaraya)));
        subAreaList.add(new SubArea("Satkhira", "Kaliganj", getString(R.string.kaliganj)));
        subAreaList.add(new SubArea("Satkhira", "Satkhira sadar", getString(R.string.satkhira_sadar)));
        subAreaList.add(new SubArea("Satkhira", "Shyamnagar", getString(R.string.shyamnagar)));
        subAreaList.add(new SubArea("Satkhira", "Tala", getString(R.string.tala)));


        subAreaList.add(new SubArea("Bagerhat", "Bagerhat sadar", getString(R.string.bagherhat_sadar)));
        subAreaList.add(new SubArea("Bagerhat", "Citolmari", getString(R.string.citolmari)));
        subAreaList.add(new SubArea("Bagerhat", "Fakirhat", getString(R.string.fakirhat)));
        subAreaList.add(new SubArea("Bagerhat", "Kocuya", getString(R.string.kochuya)));
        subAreaList.add(new SubArea("Bagerhat", "Mollahat", getString(R.string.mollarhat)));
        subAreaList.add(new SubArea("Bagerhat", "Mongla", getString(R.string.mongla)));
        subAreaList.add(new SubArea("Bagerhat", "Morolganj", getString(R.string.morolganj)));
        subAreaList.add(new SubArea("Bagerhat", "Rampal", getString(R.string.rampal)));
        subAreaList.add(new SubArea("Bagerhat", "Shoronkhola", getString(R.string.shoronkhola)));


        subAreaList.add(new SubArea("Kustia", "Veramara", getString(R.string.veramara)));
        subAreaList.add(new SubArea("Kustia", "Doulatpur", getString(R.string.doulatpur)));
        subAreaList.add(new SubArea("Kustia", "Khoksa", getString(R.string.khoksa)));
        subAreaList.add(new SubArea("Kustia", "Kumarkhali", getString(R.string.kumarkhali)));
        subAreaList.add(new SubArea("Kustia", "Kushtia sadar", getString(R.string.kustia_sadar)));
        subAreaList.add(new SubArea("Kustia", "Mirpur kushtia", getString(R.string.mirpur_kustia)));


        subAreaList.add(new SubArea("Khulna", "Batiaghata", getString(R.string.batiaghata)));
        subAreaList.add(new SubArea("Khulna", "Dakop", getString(R.string.dakop)));
        subAreaList.add(new SubArea("Khulna", "Dhigliya", getString(R.string.dhigliya)));
        subAreaList.add(new SubArea("Khulna", "Dumuriya", getString(R.string.dumuriya)));
        subAreaList.add(new SubArea("Khulna", "Fultola", getString(R.string.fultola)));
        subAreaList.add(new SubArea("Khulna", "Koyra", getString(R.string.koyra)));
        subAreaList.add(new SubArea("Khulna", "Paikgasa", getString(R.string.paikgasa)));
        subAreaList.add(new SubArea("Khulna", "Rupsa", getString(R.string.rupsa)));
        subAreaList.add(new SubArea("Khulna", "Terokhada", getString(R.string.terokhada)));
        subAreaList.add(new SubArea("Khulna", "Khulna city", getString(R.string.khulna_city)));


        subAreaList.add(new SubArea("Meherpur", "Gangni", getString(R.string.gangni)));
        subAreaList.add(new SubArea("Meherpur", "Meherpur sadar", getString(R.string.meherpur_sadar)));
        subAreaList.add(new SubArea("Meherpur", "Mujibnagar", getString(R.string.mujibnagar)));


        subAreaList.add(new SubArea("Jhenaidah", "Harinakundu", getString(R.string.harinakundu)));
        subAreaList.add(new SubArea("Jhenaidah", "Jhenaidah sadar", getString(R.string.jhenaidah_sadar)));
        subAreaList.add(new SubArea("Jhenaidah", "Kaliganj", getString(R.string.kaliganj)));
        subAreaList.add(new SubArea("Jhenaidah", "Kotchandpur", getString(R.string.kotchandpur)));
        subAreaList.add(new SubArea("Jhenaidah", "Maheshpur", getString(R.string.maheshpur)));
        subAreaList.add(new SubArea("Jhenaidah", "Shailkupa", getString(R.string.shailkupa)));


        subAreaList.add(new SubArea("Norail", "Kaliya", getString(R.string.kaliya)));
        subAreaList.add(new SubArea("Norail", "Lohagara", getString(R.string.lohagara)));
        subAreaList.add(new SubArea("Norail", "Narail sadar", getString(R.string.narail_sadar)));


        subAreaList.add(new SubArea("Magura", "Magura sadar", getString(R.string.magura_sadar)));
        subAreaList.add(new SubArea("Magura", "Mohammadpur", getString(R.string.mohammadpur)));
        subAreaList.add(new SubArea("Magura", "Shalikha", getString(R.string.shalikha)));
        subAreaList.add(new SubArea("Magura", "Sripur", getString(R.string.sripur)));


        subAreaList.add(new SubArea("Lalmonir hat", "Aditmari", getString(R.string.aditmari)));
        subAreaList.add(new SubArea("Lalmonir hat", "Hatibandha", getString(R.string.hatibandha)));
        subAreaList.add(new SubArea("Lalmonir hat", "Kaliganj", getString(R.string.kaliganj)));
        subAreaList.add(new SubArea("Lalmonir hat", "Patgram", getString(R.string.patgram)));
        subAreaList.add(new SubArea("Lalmonir hat", "Lalmonirhat sadar", getString(R.string.lalmonirhat_sadar)));


        subAreaList.add(new SubArea("Ponchogor", "Atoyari", getString(R.string.atoyari)));
        subAreaList.add(new SubArea("Ponchogor", "Boda", getString(R.string.boda)));
        subAreaList.add(new SubArea("Ponchogor", "Debiganj", getString(R.string.debiganj)));
        subAreaList.add(new SubArea("Ponchogor", "Panchagar", getString(R.string.panchagar)));
        subAreaList.add(new SubArea("Ponchogor", "tetulia", getString(R.string.tetulia)));


        subAreaList.add(new SubArea("Rangpur", "Badarganj", getString(R.string.badarganj)));
        subAreaList.add(new SubArea("Rangpur", "Gangachara", getString(R.string.gangachar)));
        subAreaList.add(new SubArea("Rangpur", "Kaunia", getString(R.string.kaunia)));
        subAreaList.add(new SubArea("Rangpur", "Mithapukur", getString(R.string.mithapukur)));
        subAreaList.add(new SubArea("Rangpur", "Pirgasa", getString(R.string.pirgasa)));
        subAreaList.add(new SubArea("Rangpur", "Pirganj", getString(R.string.pirganj)));
        subAreaList.add(new SubArea("Rangpur", "Rangpur sadar", getString(R.string.rangpur_sadar)));
        subAreaList.add(new SubArea("Rangpur", "Taraganj", getString(R.string.taraganj)));
        subAreaList.add(new SubArea("Rangpur", "Rangpur city", getString(R.string.rangpur_city)));


        subAreaList.add(new SubArea("Thakurgaon", "Baliya Dangi", getString(R.string.baliya_dangi)));
        subAreaList.add(new SubArea("Thakurgaon", "Haripur", getString(R.string.haripur)));
        subAreaList.add(new SubArea("Thakurgaon", "Pirganj", getString(R.string.pirganj)));
        subAreaList.add(new SubArea("Thakurgaon", "Ranisankail", getString(R.string.ranisankail)));
        subAreaList.add(new SubArea("Thakurgaon", "Thakurgaon sadar", getString(R.string.thakurgaon_sadar)));


        subAreaList.add(new SubArea("Kurigram", "Bhurungamari", getString(R.string.bhurangamari)));
        subAreaList.add(new SubArea("Kurigram", "Char rajibpur", getString(R.string.char_rajibpur)));
        subAreaList.add(new SubArea("Kurigram", "Chilmari", getString(R.string.chilmari)));
        subAreaList.add(new SubArea("Kurigram", "Kaliganj", getString(R.string.kaliganj)));
        subAreaList.add(new SubArea("Kurigram", "Kurigram sadar", getString(R.string.kurigram_sadar)));
        subAreaList.add(new SubArea("Kurigram", "Nageswari", getString(R.string.nageswari)));
        subAreaList.add(new SubArea("Kurigram", "Fulbari", getString(R.string.fulbari)));
        subAreaList.add(new SubArea("Kurigram", "Rajarhat", getString(R.string.rajarhat)));
        subAreaList.add(new SubArea("Kurigram", "Roumari", getString(R.string.roumari)));
        subAreaList.add(new SubArea("Kurigram", "Ulipur", getString(R.string.ulipur)));


        subAreaList.add(new SubArea("Dinajpur", "Birampur", getString(R.string.birampur)));
        subAreaList.add(new SubArea("Dinajpur", "Birganj", getString(R.string.birganj)));
        subAreaList.add(new SubArea("Dinajpur", "Bochaganj", getString(R.string.bochaganj)));
        subAreaList.add(new SubArea("Dinajpur", "Birol", getString(R.string.birol)));
        subAreaList.add(new SubArea("Dinajpur", "Chirirbandar", getString(R.string.chirirbandar)));
        subAreaList.add(new SubArea("Dinajpur", "Dinajpur sadar", getString(R.string.dinajpur_sadar)));
        subAreaList.add(new SubArea("Dinajpur", "Fulbari", getString(R.string.fulbari)));
        subAreaList.add(new SubArea("Dinajpur", "Ghoraghat", getString(R.string.ghorahat)));
        subAreaList.add(new SubArea("Dinajpur", "Hakimpur", getString(R.string.hakimpur)));
        subAreaList.add(new SubArea("Dinajpur", "Kaharol", getString(R.string.kaharol)));
        subAreaList.add(new SubArea("Dinajpur", "Khansama", getString(R.string.khansama)));
        subAreaList.add(new SubArea("Dinajpur", "Nababganj", getString(R.string.nababganj)));
        subAreaList.add(new SubArea("Dinajpur", "Parbatipur", getString(R.string.parbatipur)));


        subAreaList.add(new SubArea("Nilfamari", "Dimla", getString(R.string.dimla)));
        subAreaList.add(new SubArea("Nilfamari", "Domar", getString(R.string.domar)));
        subAreaList.add(new SubArea("Nilfamari", "Jaldhaka", getString(R.string.jaldhaka)));
        subAreaList.add(new SubArea("Nilfamari", "Kishorgonj", getString(R.string.kishorgonj)));
        subAreaList.add(new SubArea("Nilfamari", "Nilphamari sadar", getString(R.string.nilphamari_sadar)));
        subAreaList.add(new SubArea("Nilfamari", "Saidpur", getString(R.string.saidpur)));


        subAreaList.add(new SubArea("Gaibandha", "Gaibandha sadar", getString(R.string.gaibandha_sadar)));
        subAreaList.add(new SubArea("Gaibandha", "Gobindaganj", getString(R.string.gobindaganj)));
        subAreaList.add(new SubArea("Gaibandha", "Palashbari", getString(R.string.palashbari)));
        subAreaList.add(new SubArea("Gaibandha", "Fulsori", getString(R.string.fulsori)));
        subAreaList.add(new SubArea("Gaibandha", "Sadullahpur", getString(R.string.sadullahpur)));
        subAreaList.add(new SubArea("Gaibandha", "Saghata", getString(R.string.saghata)));
        subAreaList.add(new SubArea("Gaibandha", "Sundorganj", getString(R.string.sundorganj)));


        subAreaList.add(new SubArea("Bagura", "Bogra Sadar", getString(R.string.bogra_sadar)));
        subAreaList.add(new SubArea("Bagura", "Gabtoli", getString(R.string.gabtoli)));
        subAreaList.add(new SubArea("Bagura", "Sariakandi", getString(R.string.sariakandi)));
        subAreaList.add(new SubArea("Bagura", "Adamdighi", getString(R.string.adamdighi)));
        subAreaList.add(new SubArea("Bagura", "Sonatala", getString(R.string.sonatala)));
        subAreaList.add(new SubArea("Bagura", "Sherpur", getString(R.string.sherpur)));
        subAreaList.add(new SubArea("Bagura", "Kahaloo", getString(R.string.kahaloo)));
        subAreaList.add(new SubArea("Bagura", "Shibganj", getString(R.string.shibganj)));
        subAreaList.add(new SubArea("Bagura", "Dupchanchia", getString(R.string.dupchachia)));
        subAreaList.add(new SubArea("Bagura", "Nandigram", getString(R.string.nandigram)));
        subAreaList.add(new SubArea("Bagura", "Sahajanpur", getString(R.string.sahajahanpur)));
        subAreaList.add(new SubArea("Bagura", "Dhunat", getString(R.string.dhunat)));


        subAreaList.add(new SubArea("Chapainawabganj", "Gomastapur", getString(R.string.gomastapur)));
        subAreaList.add(new SubArea("Chapainawabganj", "Chapainawabganj Sadar", getString(R.string.chapainawabganj_sadar)));
        subAreaList.add(new SubArea("Chapainawabganj", "Nachole", getString(R.string.nachole)));
        subAreaList.add(new SubArea("Chapainawabganj", "Bholahat", getString(R.string.bholahat)));
        subAreaList.add(new SubArea("Chapainawabganj", "Shibganj", getString(R.string.shibganj)));


        subAreaList.add(new SubArea("Joypurhat", "Akkelpur", getString(R.string.akkelpur)));
        subAreaList.add(new SubArea("Joypurhat", "Kalai", getString(R.string.kalai)));
        subAreaList.add(new SubArea("Joypurhat", "Khetlal", getString(R.string.khetlal)));
        subAreaList.add(new SubArea("Joypurhat", "Joypurhat Sadar", getString(R.string.joypurhat_sadar)));
        subAreaList.add(new SubArea("Joypurhat", "Panchbibi", getString(R.string.panchbibi)));


        subAreaList.add(new SubArea("Nouga", "Atrai", getString(R.string.atrai)));
        subAreaList.add(new SubArea("Nouga", "Dhamoirhat", getString(R.string.dhamoirhat)));
        subAreaList.add(new SubArea("Nouga", "Niamatpur", getString(R.string.niamatpur)));
        subAreaList.add(new SubArea("Nouga", "Patnitala", getString(R.string.patnitala)));
        subAreaList.add(new SubArea("Nouga", "Porsha", getString(R.string.porsha)));
        subAreaList.add(new SubArea("Nouga", "Badalgachhi", getString(R.string.badalgachhi)));
        subAreaList.add(new SubArea("Nouga", "Mahadebpur", getString(R.string.mahadebpur)));
        subAreaList.add(new SubArea("Nouga", "Manda", getString(R.string.manda)));
        subAreaList.add(new SubArea("Nouga", "Naogaon Sadar", getString(R.string.naogaon_sadar)));
        subAreaList.add(new SubArea("Nouga", "Raninagar", getString(R.string.raninagar)));
        subAreaList.add(new SubArea("Nouga", "Sapahar", getString(R.string.sapahar)));


        subAreaList.add(new SubArea("Natore", "Gurudaspur", getString(R.string.gurudaspur)));
        subAreaList.add(new SubArea("Natore", "Naldanga", getString(R.string.naldanga)));
        subAreaList.add(new SubArea("Natore", "Natore Sadar", getString(R.string.natore_sadar)));
        subAreaList.add(new SubArea("Natore", "Baraigram", getString(R.string.baraigram)));
        subAreaList.add(new SubArea("Natore", "Bagatipara", getString(R.string.bagatipara)));
        subAreaList.add(new SubArea("Natore", "Lalpur", getString(R.string.lalpur)));
        subAreaList.add(new SubArea("Natore", "Singra", getString(R.string.singra)));


        subAreaList.add(new SubArea("Pabna", "Bera",getString(R.string.bera) ));
        subAreaList.add(new SubArea("Pabna", "Bhangura", getString(R.string.bhangura)));
        subAreaList.add(new SubArea("Pabna", "Chatmohar", getString(R.string.chatmohar)));
        subAreaList.add(new SubArea("Pabna", "Ishwardi", getString(R.string.Ishwardi)));
        subAreaList.add(new SubArea("Pabna", "Pabna Sadar", getString(R.string.Pabna_Sadar)));
        subAreaList.add(new SubArea("Pabna", "Sathia", getString(R.string.Sathia)));
        subAreaList.add(new SubArea("Pabna", "Sujanagar", getString(R.string.Sujanagar)));
        subAreaList.add(new SubArea("Pabna", "Atghoria", getString(R.string.Atghoria)));
        subAreaList.add(new SubArea("Pabna", "Faridpur", getString(R.string.faridpur)));


        subAreaList.add(new SubArea("Rajshahi", "Durgapur", getString(R.string.Durgapur)));
        subAreaList.add(new SubArea("Rajshahi", "Bagha", getString(R.string.Bagha)));
        subAreaList.add(new SubArea("Rajshahi", "Bagmara", getString(R.string.Bagmara)));
        subAreaList.add(new SubArea("Rajshahi", "Charghat", getString(R.string.Charghat)));
        subAreaList.add(new SubArea("Rajshahi", "Godagari", getString(R.string.Godagari)));
        subAreaList.add(new SubArea("Rajshahi", "Mohonpur", getString(R.string.Mohonpur)));
        subAreaList.add(new SubArea("Rajshahi", "Paba", getString(R.string.Paba)));
        subAreaList.add(new SubArea("Rajshahi", "Puthia", getString(R.string.Puthia)));
        subAreaList.add(new SubArea("Rajshahi", "Tanore", getString(R.string.Tanore)));


        subAreaList.add(new SubArea("Sirajgonj", "Belkuchi", getString(R.string.Belkuchi)));
        subAreaList.add(new SubArea("Sirajgonj", "Chauhali", getString(R.string.Chauhali)));
        subAreaList.add(new SubArea("Sirajgonj", "Kamarkhanda", getString(R.string.Kamarkhanda)));
        subAreaList.add(new SubArea("Sirajgonj", "Kazipur", getString(R.string.Kazipur)));
        subAreaList.add(new SubArea("Sirajgonj", "Raiganj", getString(R.string.Raiganj)));
        subAreaList.add(new SubArea("Sirajgonj", "Shahjadpur", getString(R.string.Shahjadpur)));
        subAreaList.add(new SubArea("Sirajgonj", "Sirajganj sadar", getString(R.string.Sirajganj_sadar)));
        subAreaList.add(new SubArea("Sirajgonj", "Tarash", getString(R.string.Tarash)));
        subAreaList.add(new SubArea("Sirajgonj", "Ullapara", getString(R.string.Ullapara)));


        subAreaList.add(new SubArea("Habiganj", "Ajmiriganj", getString(R.string.ajmiriganj)));
        subAreaList.add(new SubArea("Habiganj", "Bahubal", getString(R.string.bahubal)));
        subAreaList.add(new SubArea("Habiganj", "Baniachong", getString(R.string.baniachong)));
        subAreaList.add(new SubArea("Habiganj", "Chunarughat", getString(R.string.chunarughat)));
        subAreaList.add(new SubArea("Habiganj", "Habiganj sadar", getString(R.string.habiganj_sadar)));
        subAreaList.add(new SubArea("Habiganj", "Lakhai", getString(R.string.lakhai)));
        subAreaList.add(new SubArea("Habiganj", "Madhabpur", getString(R.string.madhabpur)));
        subAreaList.add(new SubArea("Habiganj", "Nabiganj", getString(R.string.nabiganj)));
        subAreaList.add(new SubArea("Habiganj", "Shaistaganj", getString(R.string.shaistaganj)));


        subAreaList.add(new SubArea("Moulvibazar", "Barlekha", getString(R.string.barlekha)));
        subAreaList.add(new SubArea("Moulvibazar", "Juri", getString(R.string.juri)));
        subAreaList.add(new SubArea("Moulvibazar", "Kamalganj", getString(R.string.kamalganj)));
        subAreaList.add(new SubArea("Moulvibazar", "Kulaura", getString(R.string.kulaura)));
        subAreaList.add(new SubArea("Moulvibazar", "Moulvibazar sadar", getString(R.string.moulvibazar_sadar)));
        subAreaList.add(new SubArea("Moulvibazar", "Srimangal", getString(R.string.srimangal)));


        subAreaList.add(new SubArea("Sylhet", "Belal Ganj", getString(R.string.belalganj)));
        subAreaList.add(new SubArea("Sylhet", "Biyani Bazar", getString(R.string.biyanibazar)));
        subAreaList.add(new SubArea("Sylhet", "Bishwanath", getString(R.string.bishwanath)));
        subAreaList.add(new SubArea("Sylhet", "Companiganj", getString(R.string.companiganj)));
        subAreaList.add(new SubArea("Sylhet", "Dokkhin surma", getString(R.string.dokkhin_surma)));
        subAreaList.add(new SubArea("Sylhet", "Fenchuganj", getString(R.string.fenchuganj)));
        subAreaList.add(new SubArea("Sylhet", "Gopalganj", getString(R.string.gopalganj)));
        subAreaList.add(new SubArea("Sylhet", "Goyainghat", getString(R.string.goyainghat)));
        subAreaList.add(new SubArea("Sylhet", "Jointapur", getString(R.string.jointapur)));
        subAreaList.add(new SubArea("Sylhet", "Jokiganj", getString(R.string.jokiganj)));
        subAreaList.add(new SubArea("Sylhet", "Kanaighat", getString(R.string.kanaighat)));
        subAreaList.add(new SubArea("Sylhet", "Osmani nagar", getString(R.string.osmaninagar)));
        subAreaList.add(new SubArea("Sylhet", "Sylhet sadar", getString(R.string.sylhet_sadar)));
        subAreaList.add(new SubArea("Sylhet", "Sylhet city", getString(R.string.sylhet_city)));


        subAreaList.add(new SubArea("Sunamgonj", "Bissomvorpur", getString(R.string.bissomvorpur)));
        subAreaList.add(new SubArea("Sunamgonj", "Satok", getString(R.string.satok)));
        subAreaList.add(new SubArea("Sunamgonj", "Derai", getString(R.string.derai)));
        subAreaList.add(new SubArea("Sunamgonj", "Dharmapasha", getString(R.string.dharmapasha)));
        subAreaList.add(new SubArea("Sunamgonj", "Doyarabazar", getString(R.string.doyarabazar)));
        subAreaList.add(new SubArea("Sunamgonj", "Jagannathpur", getString(R.string.jagannathpur)));
        subAreaList.add(new SubArea("Sunamgonj", "Jamalganj", getString(R.string.jamalganj)));
        subAreaList.add(new SubArea("Sunamgonj", "Salla", getString(R.string.salla)));
        subAreaList.add(new SubArea("Sunamgonj", "Madhyanagar", getString(R.string.madhyanagar)));
        subAreaList.add(new SubArea("Sunamgonj", "Sunamganj sadar", getString(R.string.sunamganj_sadar)));
        subAreaList.add(new SubArea("Sunamgonj", "Dokkhin sunamganj", getString(R.string.dokkhin_sunamganj)));
        subAreaList.add(new SubArea("Sunamgonj", "Tahirpur", getString(R.string.tahirpur)));


        subAreaList.add(new SubArea("Netrokona", "Atpara", getString(R.string.atpara)));
        subAreaList.add(new SubArea("Netrokona", "Barohatta", getString(R.string.barohatta)));
        subAreaList.add(new SubArea("Netrokona", "Durgapur", getString(R.string.durgapur)));
        subAreaList.add(new SubArea("Netrokona", "Komolakanter", getString(R.string.komolakanter)));
        subAreaList.add(new SubArea("Netrokona", "Kenduwa", getString(R.string.kenduwa)));
        subAreaList.add(new SubArea("Netrokona", "Khaliajuri", getString(R.string.khaliajuri)));
        subAreaList.add(new SubArea("Netrokona", "Modon", getString(R.string.modon)));
        subAreaList.add(new SubArea("Netrokona", "Khaliajuri", getString(R.string.mohonganj)));
        subAreaList.add(new SubArea("Netrokona", "Netrokona sodor", getString(R.string.netrokona_sadar)));
        subAreaList.add(new SubArea("Netrokona", "Purbadhala", getString(R.string.purbadhala)));


        subAreaList.add(new SubArea("Mymensingh", "Valuka", getString(R.string.valuka)));
        subAreaList.add(new SubArea("Mymensingh", "Bobaura", getString(R.string.bobaura)));
        subAreaList.add(new SubArea("Mymensingh", "Fulbariya", getString(R.string.fulbariya)));
        subAreaList.add(new SubArea("Mymensingh", "Goforgau", getString(R.string.goforgau)));
        subAreaList.add(new SubArea("Mymensingh", "Gouripur", getString(R.string.gouripur)));
        subAreaList.add(new SubArea("Mymensingh", "Haluaghat", getString(R.string.haluaghat)));
        subAreaList.add(new SubArea("Mymensingh", "Issorgonj", getString(R.string.issorganj)));
        subAreaList.add(new SubArea("Mymensingh", "Muktagacha", getString(R.string.muktagacha)));
        subAreaList.add(new SubArea("Mymensingh", "Mymensingh sodor", getString(R.string.mymensingh_sadar)));
        subAreaList.add(new SubArea("Mymensingh", "Nandail", getString(R.string.nandail)));
        subAreaList.add(new SubArea("Mymensingh", "Phulpur", getString(R.string.phulpur)));
        subAreaList.add(new SubArea("Mymensingh", "Tarakanda", getString(R.string.tarakanda)));
        subAreaList.add(new SubArea("Mymensingh", "Trishal", getString(R.string.trishal)));
        subAreaList.add(new SubArea("Mymensingh", "Mymensingh city", getString(R.string.mymensingh_city)));


        subAreaList.add(new SubArea("Jamalpur", "Bakshiganj", getString(R.string.bakshiganj)));
        subAreaList.add(new SubArea("Jamalpur", "Dewanganj", getString(R.string.dewanganj)));
        subAreaList.add(new SubArea("Jamalpur", "Islampur", getString(R.string.islampur)));
        subAreaList.add(new SubArea("Jamalpur", "Jamalpur sadar", getString(R.string.jamalpur_sadar)));
        subAreaList.add(new SubArea("Jamalpur", "Madarganj", getString(R.string.madarganj)));
        subAreaList.add(new SubArea("Jamalpur", "Melandho", getString(R.string.melandho)));
        subAreaList.add(new SubArea("Jamalpur", "Sorisabaari", getString(R.string.sorisabari)));


        subAreaList.add(new SubArea("Sherpur", "Jhinaigati", getString(R.string.jhinaigati)));
        subAreaList.add(new SubArea("Sherpur", "Nalitabari", getString(R.string.nalitabari)));
        subAreaList.add(new SubArea("Sherpur", "Nokla", getString(R.string.nokla)));
        subAreaList.add(new SubArea("Sherpur", "Sherpur Sadar", getString(R.string.sherpur_sadar)));
        subAreaList.add(new SubArea("Sherpur", "Sreebordi", getString(R.string.sreebordi)));


    }

}