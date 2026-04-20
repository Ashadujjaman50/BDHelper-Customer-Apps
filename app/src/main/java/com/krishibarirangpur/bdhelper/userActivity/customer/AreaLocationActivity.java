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
import com.krishibarirangpur.bdhelper.model.AreaModel;
import com.krishibarirangpur.bdhelper.model.CityModel;
import com.krishibarirangpur.bdhelper.model.SubAreaModel;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class AreaLocationActivity extends BaseActivity {

    private ActivityAreaLocationBinding binding;

    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;
    private SubAreaAdapter subAreaAdapter;
    private List<CityModel> cityModelList;
    private List<AreaModel> areaModelList;
    private List<SubAreaModel> subAreaModelList;
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

                CityModel currentCity = cityModelList.get(position);
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
                AreaModel currentArea = areaModelList.get(position);
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
                SubAreaModel currentSubAreaModel = subAreaModelList.get(position);
                //selectedSubAreaId = currentSubArea.getSubAreaId();
                selectedSubAreaName = currentSubAreaModel.getSubAreaName();

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
        cityAdapter = new CityAdapter(cityModelList);
        binding.cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cityRecyclerView.setAdapter(cityAdapter);

        binding.areaRecyclerView.setHasFixedSize(true);
        areaAdapter = new AreaAdapter(areaModelList);
        binding.areaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.areaRecyclerView.setAdapter(areaAdapter);

        binding.subAreaRecyclerView.setHasFixedSize(true);
        subAreaAdapter = new SubAreaAdapter(subAreaModelList);
        binding.subAreaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.subAreaRecyclerView.setAdapter(subAreaAdapter);
    }


    private void fillCityList() {
        cityModelList = new ArrayList<>();
        cityModelList.add(new CityModel("1", getString(R.string.dhaka_city)));
        cityModelList.add(new CityModel("2", getString(R.string.chittagong_city)));
        cityModelList.add(new CityModel("Dhaka", getString(R.string.dhaka_division)));
        cityModelList.add(new CityModel("Barisal", getString(R.string.barisal_division)));
        cityModelList.add(new CityModel("Chittagong", getString(R.string.chittagong_division)));
        cityModelList.add(new CityModel("Mymensingh", getString(R.string.mymensingh_division)));
        cityModelList.add(new CityModel("Khulna", getString(R.string.khulna_division)));
        cityModelList.add(new CityModel("Rajshahi", getString(R.string.rajshahi_division)));
        cityModelList.add(new CityModel("Rangpur", getString(R.string.rangpur_division)));
        cityModelList.add(new CityModel("Sylhet", getString(R.string.sylhet_division)));

    }

    private void fillAreaList() {
        areaModelList = new ArrayList<>();
        areaModelList.add(new AreaModel("1", "Uttara", getString(R.string.uttara)));
        areaModelList.add(new AreaModel("1", "Kafrul", getString(R.string.kafrul)));
        areaModelList.add(new AreaModel("1", "KamrangirChor", getString(R.string.kamrangir_char)));
        areaModelList.add(new AreaModel("1", "Kotoyaly", getString(R.string.kotoyali)));
        areaModelList.add(new AreaModel("1", "Cantonment", getString(R.string.cantonment)));
        areaModelList.add(new AreaModel("1", "Khilgaon", getString(R.string.khilgaon)));
        areaModelList.add(new AreaModel("1", "Gulshan", getString(R.string.gulsan)));
        areaModelList.add(new AreaModel("1", "Demra", getString(R.string.demra)));
        areaModelList.add(new AreaModel("1", "Tejgaon", getString(R.string.tejgaon)));
        areaModelList.add(new AreaModel("1", "Dhanmondi", getString(R.string.dhanmondi)));
        areaModelList.add(new AreaModel("1", "Pollobi", getString(R.string.pollobi)));
        areaModelList.add(new AreaModel("1", "Bosundhara", getString(R.string.bosundhara)));
        areaModelList.add(new AreaModel("1", "Badda", getString(R.string.badda)));
        areaModelList.add(new AreaModel("1", "Motijhil", getString(R.string.motijhil)));
        areaModelList.add(new AreaModel("1", "Mirpur", getString(R.string.mirpur)));
        areaModelList.add(new AreaModel("1", "Mohammadpur", getString(R.string.mohammadpur)));
        areaModelList.add(new AreaModel("1", "Ramona", getString(R.string.ramona)));
        areaModelList.add(new AreaModel("1", "Lalbag", getString(R.string.lalbug)));
        areaModelList.add(new AreaModel("1", "Shampur", getString(R.string.shampur)));
        areaModelList.add(new AreaModel("1", "ShobujBug", getString(R.string.sobuj_bug)));
        areaModelList.add(new AreaModel("1", "Sutrapur", getString(R.string.sutrapur)));
        areaModelList.add(new AreaModel("1", "Hajaribug", getString(R.string.hajaribug)));


        areaModelList.add(new AreaModel("2", "Akborshah", getString(R.string.akbor_shah)));
        areaModelList.add(new AreaModel("2", "Andharkilla", getString(R.string.andhar_killa)));
        areaModelList.add(new AreaModel("2", "AmirbugAbashikalaka", getString(R.string.amirbug_abashik_alaka)));
        areaModelList.add(new AreaModel("2", "Alishapara", getString(R.string.ali_shapara)));
        areaModelList.add(new AreaModel("2", "Asadgonjcommercialarea", getString(R.string.asadganj_commercial_area)));
        areaModelList.add(new AreaModel("2", "EPZ", getString(R.string.epz)));
        areaModelList.add(new AreaModel("2", "Uttarpathantuli", getString(R.string.uttar_pathantuli)));
        areaModelList.add(new AreaModel("2", "Anayetbazar", getString(R.string.anayet_bazar)));
        areaModelList.add(new AreaModel("2", "KarnafuliResidentialarea", getString(R.string.karnafuli_residential_area)));
        areaModelList.add(new AreaModel("2", "Kotoyali", getString(R.string.kotoyali2)));
        areaModelList.add(new AreaModel("2", "Khatungonj", getString(R.string.khatungonj)));
        areaModelList.add(new AreaModel("2", "Khulshi", getString(R.string.khulshi)));
        areaModelList.add(new AreaModel("2", "Gosaildanga", getString(R.string.gosaildanga)));
        areaModelList.add(new AreaModel("2", "Chowkbazar", getString(R.string.chowk_bazar)));
        areaModelList.add(new AreaModel("2", "Chattagrambondor", getString(R.string.chattagram_bondor)));
        areaModelList.add(new AreaModel("2", "Chorpara", getString(R.string.chor_para)));

        areaModelList.add(new AreaModel("2", "Chorhalda", getString(R.string.chor_halda)));
        areaModelList.add(new AreaModel("2", "Chadgao", getString(R.string.chadgao)));
        areaModelList.add(new AreaModel("2", "ChowdhuryPara", getString(R.string.chowdhury_para)));
        areaModelList.add(new AreaModel("2", "GEMofficerscolony", getString(R.string.gem_officers_colony)));
        areaModelList.add(new AreaModel("2", "Jhawtola", getString(R.string.jhawtola)));
        areaModelList.add(new AreaModel("2", "Tigerpassrailwaycolony", getString(R.string.tigerpass_railway_colony)));
        areaModelList.add(new AreaModel("2", "TSPcolony", getString(R.string.tsp_colony)));
        areaModelList.add(new AreaModel("2", "Teribazar", getString(R.string.teri_bazar)));
        areaModelList.add(new AreaModel("2", "Doublemuring", getString(R.string.double_muring)));
        areaModelList.add(new AreaModel("2", "Doijpara", getString(R.string.doijpara)));
        areaModelList.add(new AreaModel("2", "Southpatenga", getString(R.string.south_patenga)));
        areaModelList.add(new AreaModel("2", "Southbondar", getString(R.string.south_bondar)));
        areaModelList.add(new AreaModel("2", "Dokkhin moddho holyshohor", getString(R.string.dokkhin_moddho_holyshohor)));
        areaModelList.add(new AreaModel("2", "Dampara", getString(R.string.dampara)));
        areaModelList.add(new AreaModel("2", "Deoyanghat", getString(R.string.deoyan_ghat)));
        areaModelList.add(new AreaModel("2", "Dewanbazar", getString(R.string.deoyan_bazar)));
        areaModelList.add(new AreaModel("2", "Dhumpara", getString(R.string.dhumpara)));
        areaModelList.add(new AreaModel("2", "Northmiddleholishohor", getString(R.string.north_middle_holishohor)));
        areaModelList.add(new AreaModel("2", "Nasirabad", getString(R.string.nasirabadh)));
        areaModelList.add(new AreaModel("2", "Newmuring", getString(R.string.new_muring)));
        areaModelList.add(new AreaModel("2", "Navyport", getString(R.string.navy_port)));
        areaModelList.add(new AreaModel("2", "Podmaabashikalaka", getString(R.string.podma_abashik_alaka)));
        areaModelList.add(new AreaModel("2", "Paslaish", getString(R.string.paslaish)));
        areaModelList.add(new AreaModel("2", "Pathantuli", getString(R.string.pathantuli)));
        areaModelList.add(new AreaModel("2", "Potenga", getString(R.string.potenga)));
        areaModelList.add(new AreaModel("2", "Patharghata", getString(R.string.patharghata)));
        areaModelList.add(new AreaModel("2", "Pahartali", getString(R.string.pahartali)));
        areaModelList.add(new AreaModel("2", "Purbonimtala", getString(R.string.purbo_nimtala)));
        areaModelList.add(new AreaModel("2", "Purbomadarbari", getString(R.string.purbo_madar_bari)));
        areaModelList.add(new AreaModel("2", "Bahaddarhat", getString(R.string.bahaddarhat)));
        areaModelList.add(new AreaModel("2", "Bangladeshbankcolony", getString(R.string.bangladesh_bank_colony)));
        areaModelList.add(new AreaModel("2", "bakolia", getString(R.string.bakolia)));
        areaModelList.add(new AreaModel("2", "Bayazidbostami", getString(R.string.bayazid_bostami)));
        areaModelList.add(new AreaModel("2", "Mansurabad", getString(R.string.mansurabad)));
        areaModelList.add(new AreaModel("2", "Maijpara", getString(R.string.majipara)));
        areaModelList.add(new AreaModel("2", "Rangiparabankcolony", getString(R.string.rangipara_bank_colony)));
        areaModelList.add(new AreaModel("2", "Laldairchar", getString(R.string.laldair_char)));
        areaModelList.add(new AreaModel("2", "Shadorghat", getString(R.string.shadorghat)));
        areaModelList.add(new AreaModel("2", "Sondippara", getString(R.string.sondip_para)));
        areaModelList.add(new AreaModel("2", "Southagrabad", getString(R.string.south_agrabad)));
        areaModelList.add(new AreaModel("2", "CGScolony", getString(R.string.cgs_colony)));
        areaModelList.add(new AreaModel("2", "Hali_shohor", getString(R.string.hali_shohor)));
        areaModelList.add(new AreaModel("2", "Halishohormunshipara", getString(R.string.hali_shohor_munshipara)));
        areaModelList.add(new AreaModel("2", "Halishohorsenanibash", getString(R.string.hali_shohor_senanibash)));
        areaModelList.add(new AreaModel("2", "Hosenahmedpara", getString(R.string.hosen_ahmedpara)));


        areaModelList.add(new AreaModel("Dhaka", "Kishorgonj", getString(R.string.kishorgonj)));
        areaModelList.add(new AreaModel("Dhaka", "Gazipur", getString(R.string.gazipur)));
        areaModelList.add(new AreaModel("Dhaka", "Gopalgonj", getString(R.string.gopalgonj)));
        areaModelList.add(new AreaModel("Dhaka", "Tangail", getString(R.string.tangail)));
        areaModelList.add(new AreaModel("Dhaka", "Dhaka", getString(R.string.dhaka)));
        areaModelList.add(new AreaModel("Dhaka", "Narsingdi", getString(R.string.narsingdi)));
        areaModelList.add(new AreaModel("Dhaka", "NarayanGanj", getString(R.string.narayanganj)));
        areaModelList.add(new AreaModel("Dhaka", "Faridpur", getString(R.string.faridpur)));
        areaModelList.add(new AreaModel("Dhaka", "Madaripur", getString(R.string.madaripur)));
        areaModelList.add(new AreaModel("Dhaka", "Manikgonj", getString(R.string.manikgonj)));
        areaModelList.add(new AreaModel("Dhaka", "Munshigonj", getString(R.string.munshigonj)));
        areaModelList.add(new AreaModel("Dhaka", "Rajbari", getString(R.string.rajbari)));
        areaModelList.add(new AreaModel("Dhaka", "Shariatpur", getString(R.string.shariatpur)));


        areaModelList.add(new AreaModel("Barisal", "Jhalokati", getString(R.string.jhalokati)));
        areaModelList.add(new AreaModel("Barisal", "Patuakhali", getString(R.string.patuakhali)));
        areaModelList.add(new AreaModel("Barisal", "Pirojpur", getString(R.string.pirojpur)));
        areaModelList.add(new AreaModel("Barisal", "Barguna", getString(R.string.barguna)));
        areaModelList.add(new AreaModel("Barisal", "Barisal", getString(R.string.barisal)));
        areaModelList.add(new AreaModel("Barisal", "Bhola", getString(R.string.bhola)));


        areaModelList.add(new AreaModel("Chittagong", "CoxsBazar", getString(R.string.cox_bazar)));
        areaModelList.add(new AreaModel("Chittagong", "Comilla", getString(R.string.comilla)));
        areaModelList.add(new AreaModel("Chittagong", "Khagrasori", getString(R.string.khagrasori)));
        areaModelList.add(new AreaModel("Chittagong", "Chittagong", getString(R.string.chattogram)));
        areaModelList.add(new AreaModel("Chittagong", "Chandpur", getString(R.string.chandpur)));
        areaModelList.add(new AreaModel("Chittagong", "Noakhali", getString(R.string.noakhali)));
        areaModelList.add(new AreaModel("Chittagong", "Feni", getString(R.string.feni)));
        areaModelList.add(new AreaModel("Chittagong", "Bandarban", getString(R.string.bandarban)));
        areaModelList.add(new AreaModel("Chittagong", "Brahmanbaria", getString(R.string.brahmanbaria)));
        areaModelList.add(new AreaModel("Chittagong", "Rangamati", getString(R.string.rangamati)));
        areaModelList.add(new AreaModel("Chittagong", "Lokkhipur", getString(R.string.lokkhipur)));

        areaModelList.add(new AreaModel("Mymensingh", "Jamalpur", getString(R.string.jamalpur)));
        areaModelList.add(new AreaModel("Mymensingh", "Netrokona", getString(R.string.netrokona)));
        areaModelList.add(new AreaModel("Mymensingh", "Mymensingh", getString(R.string.mymensingh)));
        areaModelList.add(new AreaModel("Mymensingh", "Sherpur", getString(R.string.sherpur)));

        areaModelList.add(new AreaModel("Khulna", "Kustia", getString(R.string.kustia)));
        areaModelList.add(new AreaModel("Khulna", "Khulna", getString(R.string.khulna)));
        areaModelList.add(new AreaModel("Khulna", "Chuadanga", getString(R.string.chuadanga)));
        areaModelList.add(new AreaModel("Khulna", "Jhenaidah", getString(R.string.jhenaidah)));
        areaModelList.add(new AreaModel("Khulna", "Norail", getString(R.string.norail)));
        areaModelList.add(new AreaModel("Khulna", "Bagerhat", getString(R.string.bagerhat)));
        areaModelList.add(new AreaModel("Khulna", "Magura", getString(R.string.magura)));
        areaModelList.add(new AreaModel("Khulna", "Meherpur", getString(R.string.meherpur)));
        areaModelList.add(new AreaModel("Khulna", "Jessore", getString(R.string.jessore)));
        areaModelList.add(new AreaModel("Khulna", "Satkhira", getString(R.string.satkhira)));

        areaModelList.add(new AreaModel("Rajshahi", "Chapainawabganj", getString(R.string.chapainawabganj)));
        areaModelList.add(new AreaModel("Rajshahi", "Joypurhat", getString(R.string.joypurhat)));
        areaModelList.add(new AreaModel("Rajshahi", "Nouga", getString(R.string.nouga)));
        areaModelList.add(new AreaModel("Rajshahi", "Natore", getString(R.string.natore)));
        areaModelList.add(new AreaModel("Rajshahi", "Pabna", getString(R.string.pabna)));
        areaModelList.add(new AreaModel("Rajshahi", "Bagura", getString(R.string.bagura)));
        areaModelList.add(new AreaModel("Rajshahi", "Rajshahi", getString(R.string.rajshahi)));
        areaModelList.add(new AreaModel("Rajshahi", "Sirajgonj", getString(R.string.sirajgonj)));

        areaModelList.add(new AreaModel("Rangpur", "Kurigram", getString(R.string.kurigram)));
        areaModelList.add(new AreaModel("Rangpur", "Gaibandha", getString(R.string.gaibandha)));
        areaModelList.add(new AreaModel("Rangpur", "Thakurgaon", getString(R.string.thakurgaon)));
        areaModelList.add(new AreaModel("Rangpur", "Dinajpur", getString(R.string.dinajpur)));
        areaModelList.add(new AreaModel("Rangpur", "Nilfamari", getString(R.string.nilfamari)));
        areaModelList.add(new AreaModel("Rangpur", "Ponchogor", getString(R.string.ponchogor)));
        areaModelList.add(new AreaModel("Rangpur", "Rangpur", getString(R.string.rangpur)));
        areaModelList.add(new AreaModel("Rangpur", "Lalmoni", getString(R.string.lalmoni)));

        areaModelList.add(new AreaModel("Sylhet", "Moulvibazar", getString(R.string.moulvibazar)));
        areaModelList.add(new AreaModel("Sylhet", "Sylhet", getString(R.string.sylhet)));
        areaModelList.add(new AreaModel("Sylhet", "Sunamgonj", getString(R.string.sunamganj)));
        areaModelList.add(new AreaModel("Sylhet", "Habiganj", getString(R.string.habiganj)));

    }

    private void fillSubAreaList() {
        subAreaModelList = new ArrayList<>();
        subAreaModelList.add(new SubAreaModel("Uttara", "Aisnubug", getString(R.string.aisnubug)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Ajompur", getString(R.string.ajompur)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Anurbug", getString(R.string.anurbug)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Abdullahpur", getString(R.string.abdullahpur)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Amtola", getString(R.string.amtola)));
        subAreaModelList.add(new SubAreaModel("Uttara", "AshiyanCity", getString(R.string.asiyan_city)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Ahalia", getString(R.string.ahalia)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Uttorkhan", getString(R.string.uttorkhan)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Kaola", getString(R.string.kaola)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Kajibari", getString(R.string.kajibari)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Kamarpara", getString(R.string.kamarpara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Khilkhet", getString(R.string.khilkhet)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Gaoyair", getString(R.string.gaoyair)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Joshimuddin", getString(R.string.joshimuddin)));
        subAreaModelList.add(new SubAreaModel("Uttara", "DhakaAirport", getString(R.string.dhaka_airport)));
        subAreaModelList.add(new SubAreaModel("Uttara", "SouthMollertech", getString(R.string.south_mollertech)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Dokkhinkhan", getString(R.string.dokkhinkhan)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Diyabari", getString(R.string.diyabari)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Deoyanpara", getString(R.string.deoyanpara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Dhour", getString(R.string.dhour)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Noddapara", getString(R.string.noddapara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "NoyaNogor", getString(R.string.noya_nogor)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Fulbaria", getString(R.string.fulbaria)));
        subAreaModelList.add(new SubAreaModel("Uttara", "RomnarTech", getString(R.string.romnar_tech)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Bepari Bari", getString(R.string.bepari_bari)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Vatira", getString(R.string.vatira)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Viyapara", getString(R.string.viyapara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "ModdhoPara", getString(R.string.moddho_para)));
        subAreaModelList.add(new SubAreaModel("Uttara", "MadarBari", getString(R.string.madar_bari)));
        subAreaModelList.add(new SubAreaModel("Uttara", "RajBari", getString(R.string.raj_bari)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Shekherpara", getString(R.string.shekherpara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Shonakhola", getString(R.string.shona_khola)));
        subAreaModelList.add(new SubAreaModel("Uttara", "Hajipara", getString(R.string.hajipara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "1nosectorUttara", getString(R.string.viyapara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "10 no. sector Uttara", getString(R.string._10_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "11 no. sector Uttara", getString(R.string._11_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "12 no. sector Uttara", getString(R.string._12_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "13 no. sector Uttara", getString(R.string._13_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "14 no. sector Uttara", getString(R.string._14_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "15 no. sector Uttara", getString(R.string._15_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "16 no. sector Uttara", getString(R.string._16_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "17 no. sector Uttara", getString(R.string._17_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "2 no. sector Uttara", getString(R.string._2_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "3 no. sector Uttara", getString(R.string._3_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "4 no. sector Uttara", getString(R.string._4_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "5 no. sector Uttara", getString(R.string._5_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "6 no. sector Uttara", getString(R.string._6_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "7 no. sector Uttara", getString(R.string._7_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "8 no. sector Uttara", getString(R.string._8_no_sector_uttara)));
        subAreaModelList.add(new SubAreaModel("Uttara", "9 no. sector Uttara", getString(R.string._9_no_sector_uttara)));


        subAreaModelList.add(new SubAreaModel("Kafrul", "Ajijpolli", getString(R.string.ajij_polli)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Dhaka navy colony", getString(R.string.dhaka_navy_colony)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Damal kot", getString(R.string.dhamal_kot)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Vasantech", getString(R.string.vasantech)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Mohakhali dohs", getString(R.string.mohakhali_dohs)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Matikata", getString(R.string.matikata)));
        subAreaModelList.add(new SubAreaModel("Kafrul", "Soudi koloni", getString(R.string.soudi_koloni)));


        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Abu soiyod bazar", getString(R.string.abu_soiyod_bazar)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Koyla ghat", getString(R.string.koyla_ghat)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Karim bug", getString(R.string.karim_bug)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Nurburg", getString(R.string.nurbug)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Munsi hut", getString(R.string.munshi_hat)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Muslimbug", getString(R.string.muslimbug)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Zaullar hati chourasta", getString(R.string.zaullar_hati_chourasta)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Sultanganj", getString(R.string.sultanganj)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Hajurpara", getString(R.string.hajirpara)));
        subAreaModelList.add(new SubAreaModel("KamrangirChor", "Hasan nagor", getString(R.string.hasan_nagor)));


        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Islampur", getString(R.string.islampur)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Jinda bazar", getString(R.string.jinda_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Tati bazar", getString(R.string.tati_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Noya bazar", getString(R.string.noya_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Potuyatoli", getString(R.string.potuyatoli)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Badam toli", getString(R.string.badam_toli)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Ray saheb bazar", getString(R.string.ray_ssaheb_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Lokkhi Bazar", getString(R.string.lokkhi_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyaly", "Sadarghat", getString(R.string.sadarghat)));


        subAreaModelList.add(new SubAreaModel("Cantonment", "Ecb chottor", getString(R.string.ecb_chottor)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "MES colony", getString(R.string.mes_colony)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Kalibari", getString(R.string.kalibari)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Noya bazar", getString(R.string.noya_bazar)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Kurmitola", getString(R.string.kirmitola)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Goaltech", getString(R.string.goaltech)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Jamtola", getString(R.string.jamtola)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Zia colony", getString(R.string.zia_colony)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Dali market", getString(R.string.dali_market)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Deoyanpara", getString(R.string.deoyanpara)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Nikung", getString(R.string.nikung)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Nirjhor", getString(R.string.nirjhor)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Barontech", getString(R.string.barontech)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Balurghat", getString(R.string.balurghat)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Manikdi", getString(R.string.manikdi)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Mastertech", getString(R.string.mastertech)));
        subAreaModelList.add(new SubAreaModel("Cantonment", "Mostafa kamal chottor", getString(R.string.mostafa_kamal_chottor)));


        subAreaModelList.add(new SubAreaModel("Khilgaon", "Adarsh bug", getString(R.string.adarsh)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Ansar bug", getString(R.string.ansar_bug)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Aftab Nagar", getString(R.string.aftab_nagar)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Ulon", getString(R.string.ulon)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Khilgaon bagicha", getString(R.string.khilgoan_bagicha)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Khilgaon block -a", getString(R.string.khilgoan_block_a)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Khilgaon block -b", getString(R.string.khilgoan_block_b)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Khilgaon block -c", getString(R.string.khilgoan_block_c)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Khilgaon railgate", getString(R.string.khilgoan_railgate)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Goran", getString(R.string.goran)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Chowdhury Para", getString(R.string.chowdhury_para)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Tilpa para", getString(R.string.tilpa_para)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Notun bug", getString(R.string.notun_bug)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Nondi para", getString(R.string.nondi_para)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Bonosri", getString(R.string.bonosri)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Malibog", getString(R.string.malibug)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Meradiya", getString(R.string.meradiya)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Rampura", getString(R.string.rampura)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Riyaj bug", getString(R.string.riyai_bug)));
        subAreaModelList.add(new SubAreaModel("Khilgaon", "Sipahi Bug", getString(R.string.sipahi_bug)));


        subAreaModelList.add(new SubAreaModel("Gulshan", "Korail", getString(R.string.korail)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Gulshan avenue", getString(R.string.gulshan_avenue)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Gulshan 2", getString(R.string.gulshan_2)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Gulshan 1", getString(R.string.gulshan_1)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Niketon", getString(R.string.niketon)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Bonani", getString(R.string.bonani)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Bari dhara", getString(R.string.bari_dhara)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Bari dhara dohs", getString(R.string.bari_dhara_dohs)));
        subAreaModelList.add(new SubAreaModel("Gulshan", "Mohakhali", getString(R.string.mohakhali)));


        subAreaModelList.add(new SubAreaModel("Demra", "Ahmed Nagar", getString(R.string.ahmed_nagar)));
        subAreaModelList.add(new SubAreaModel("Demra", "Konapara", getString(R.string.konapara)));
        subAreaModelList.add(new SubAreaModel("Demra", "Green model town", getString(R.string.green_model_town)));
        subAreaModelList.add(new SubAreaModel("Demra", "Chonpara", getString(R.string.chonpara)));
        subAreaModelList.add(new SubAreaModel("Demra", "Dogair", getString(R.string.dogair)));
        subAreaModelList.add(new SubAreaModel("Demra", "Tarabo", getString(R.string.tarabo)));
        subAreaModelList.add(new SubAreaModel("Demra", "South rupsi", getString(R.string.south_rupsi)));
        subAreaModelList.add(new SubAreaModel("Demra", "Naopara", getString(R.string.naopara)));
        subAreaModelList.add(new SubAreaModel("Demra", "Bamoli bazar", getString(R.string.bamoli_bazar)));
        subAreaModelList.add(new SubAreaModel("Demra", "Bahir tengra", getString(R.string.bahir_tengra)));
        subAreaModelList.add(new SubAreaModel("Demra", "Rasul nagor", getString(R.string.rasul_nagor)));
        subAreaModelList.add(new SubAreaModel("Demra", "Sanar para", getString(R.string.saar_para)));
        subAreaModelList.add(new SubAreaModel("Demra", "Saruliya", getString(R.string.saruliya)));
        subAreaModelList.add(new SubAreaModel("Demra", "Haji nagor", getString(R.string.haji_nagor)));


        subAreaModelList.add(new SubAreaModel("Tejgaon", "Kawran Bazar", getString(R.string.kawran_bazar)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Tejkuni para", getString(R.string.tejkuni_para)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Nakhal para", getString(R.string.nakhal_para)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Tejturi bazar", getString(R.string.tejturi_bazar)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Nabisko", getString(R.string.nabisko)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Farmgate", getString(R.string.farmgate)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Monipuripara", getString(R.string.monipuripara)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Rasul bug", getString(R.string.rsul_bug)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Raja bazar", getString(R.string.raja_bazar)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Shahin bug", getString(R.string.shahin_bug)));
        subAreaModelList.add(new SubAreaModel("Tejgaon", "Shukrabad", getString(R.string.shukrabad)));


        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Kalabagan", getString(R.string.kalabagan)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Green road", getString(R.string.green_road)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Jiga tola", getString(R.string.jiga_tola)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -1", getString(R.string.dhanmondi_1)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -2", getString(R.string.dhanmondi_2)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -3", getString(R.string.dhanmondi_3)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -4", getString(R.string.dhanmondi_4)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -5", getString(R.string.dhanmondi_5)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -6", getString(R.string.dhanmondi_6)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -7", getString(R.string.dhanmondi_7)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -8A", getString(R.string.dhanmondi_8a)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -9A", getString(R.string.dhanmondi_9a)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -10A", getString(R.string.dhanmondi_10a)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -11", getString(R.string.dhanmondi_11)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -12", getString(R.string.dhanmondi_12)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -13", getString(R.string.dhanmondi_13)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -14", getString(R.string.dhanmondi_14)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -15", getString(R.string.dhanmondi_15)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -27", getString(R.string.dhanmondi_27)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Dhanmondi -32", getString(R.string.dhanmondi_32)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Newmarket", getString(R.string.newmarket)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Panthapath", getString(R.string.panthapath)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Shankar", getString(R.string.kalashankarbagan)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Shobhan Bug", getString(R.string.shobhan_bug)));
        subAreaModelList.add(new SubAreaModel("Dhanmondi", "Shukrabad", getString(R.string.shukrabad)));


        subAreaModelList.add(new SubAreaModel("Pollobi", "Kalshi", getString(R.string.kalshi)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Duip nagar", getString(R.string.duip_nagar)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Bordhito polli", getString(R.string.bordhito_polli)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Mirpur dohs", getString(R.string.mirpur_dohs)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Mirpur senanibas", getString(R.string.mirpur_senanibas)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Shagufta", getString(R.string.shagufta)));
        subAreaModelList.add(new SubAreaModel("Pollobi", "Section-12 mirpur", getString(R.string.section_12_mirpur)));


        subAreaModelList.add(new SubAreaModel("Bosundhara", "Kuril", getString(R.string.kuril)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Cocacola", getString(R.string.cococola)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Nodda", getString(R.string.nodda)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Neela market", getString(R.string.neela_market)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -i", getString(R.string.bashundhara_block_i)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -e", getString(R.string.bashundhara_block_e)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -A", getString(R.string.bashundhara_block_a)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -H", getString(R.string.bashundhara_block_h)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -F", getString(R.string.bashundhara_block_f)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -M", getString(R.string.bashundhara_block_m)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -L", getString(R.string.bashundhara_block_L)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -K", getString(R.string.bashundhara_block_k)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -g", getString(R.string.bashundhara_block_g)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -j", getString(R.string.bashundhara_block_j)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -d", getString(R.string.bashundhara_block_d)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -b", getString(R.string.bashundhara_block_b)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Bashundhara block -c", getString(R.string.bashundhara_block_c)));
        subAreaModelList.add(new SubAreaModel("Bosundhara", "Beraid", getString(R.string.beraid)));


        subAreaModelList.add(new SubAreaModel("Badda", "Namapara", getString(R.string.namapara)));
        subAreaModelList.add(new SubAreaModel("Badda", "Nurerchala", getString(R.string.benurerchalaraid)));
        subAreaModelList.add(new SubAreaModel("Badda", "East nurer chala", getString(R.string.east_nurer_chala)));
        subAreaModelList.add(new SubAreaModel("Badda", "Bayou La para", getString(R.string.beyou_la_para)));
        subAreaModelList.add(new SubAreaModel("Badda", "Badda D I T project", getString(R.string.badda_dit_project)));
        subAreaModelList.add(new SubAreaModel("Badda", "Badda link road", getString(R.string.badda_link_road)));
        subAreaModelList.add(new SubAreaModel("Badda", "Bepari bari", getString(R.string.bepari_bari)));
        subAreaModelList.add(new SubAreaModel("Badda", "Middle badda", getString(R.string.middle_badda)));
        subAreaModelList.add(new SubAreaModel("Badda", "Merul", getString(R.string.merul)));
        subAreaModelList.add(new SubAreaModel("Badda", "Vatara", getString(R.string.vatara)));
        subAreaModelList.add(new SubAreaModel("Badda", "Merul badda", getString(R.string.merul_badda)));
        subAreaModelList.add(new SubAreaModel("Badda", "Rup nagar", getString(R.string.rup_nagar)));
        subAreaModelList.add(new SubAreaModel("Badda", "Shahjadpur", getString(R.string.shahjadpur)));
        subAreaModelList.add(new SubAreaModel("Badda", "Solmaid", getString(R.string.solmaid)));


        subAreaModelList.add(new SubAreaModel("Motijhil", "Fakirapul", getString(R.string.fakirapul)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Motijhil", getString(R.string.motijhil)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Razarbug", getString(R.string.razarbug)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Shahidbug", getString(R.string.shahidbug)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Shanti nagar", getString(R.string.bershanti_nagaraid)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Shanti bug", getString(R.string.shanti_bug)));
        subAreaModelList.add(new SubAreaModel("Motijhil", "Shahjahanpur", getString(R.string.shahjahanpur)));


        subAreaModelList.add(new SubAreaModel("Mirpur", "Gabtoli", getString(R.string.gabtoli)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Zoo", getString(R.string.zoo)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Zahurabad", getString(R.string.zahurabad)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Tolarbug", getString(R.string.tolarbug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "South monipur", getString(R.string.south_monipur)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Nonderbug", getString(R.string.nonderbug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Nobaberbug", getString(R.string.nobaberbug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "West kazipara", getString(R.string.west_kazipara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "West shewrapara", getString(R.string.west_shewrapara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Paikpara", getString(R.string.paikpara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Palapara", getString(R.string.palapara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Pirer bug", getString(R.string.pirerbug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "East kazipara", getString(R.string.est_kazipara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "East shewrapara", getString(R.string.east_shewrapara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Boshupara", getString(R.string.boshupara)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Baten nagar", getString(R.string.baten_nagar)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Beribadh", getString(R.string.beribadh)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Borobug", getString(R.string.borobug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Modina nagar", getString(R.string.modina_nagar)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Monipur", getString(R.string.monipur)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -6", getString(R.string.mirpur_6)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -2", getString(R.string.mirpur_2)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -60 feet", getString(R.string.mirpur_60_feet)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur rupnagar abashik alaka", getString(R.string.mirpur_rupnagar_abashik_alaka)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -11", getString(R.string.mirpur_11)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -12", getString(R.string.mirpur_12)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -10", getString(R.string.mirpur_10)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -13", getString(R.string.mirpur_13)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -14", getString(R.string.mirpur_14)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mirpur -1", getString(R.string.mirpur_1)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Mohammadiya society", getString(R.string.mohammadiya_society)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Rainkhola", getString(R.string.rainlhola)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Shain pukur", getString(R.string.shain_pukur)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Shah ali bug", getString(R.string.shah_ali_bug)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Shimul tala", getString(R.string.shimul_tala)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Lalkuthi", getString(R.string.lalkuthi)));
        subAreaModelList.add(new SubAreaModel("Mirpur", "Hariram pur", getString(R.string.hariram_pur)));


        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Adabar", getString(R.string.adabar)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Ashadget", getString(R.string.ashadget)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Chadmiya housing", getString(R.string.chadmiya_housing)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Jafrabad", getString(R.string.jafrabad)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Tajmohol road", getString(R.string.tajmohol_road)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Bosila", getString(R.string.bosila)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Baitul aman housing", getString(R.string.baitul_aman_housing)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Mohammadiya abashik alaka", getString(R.string.mohammadiya_abashik_alaka)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Ring road", getString(R.string.ringroad)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Lalmatia", getString(R.string.lalmatia)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Shongkar", getString(R.string.shongkar)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Shekhertek", getString(R.string.shekhertek)));
        subAreaModelList.add(new SubAreaModel("Mohammadpur", "Shamoli", getString(R.string.shamoli)));


        subAreaModelList.add(new SubAreaModel("Ramona", "Elephant road", getString(R.string.elephant_road)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Old iskaton", getString(R.string.old_iskaton)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Kakrail", getString(R.string.kakrail)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Katabon", getString(R.string.katabon)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Dhaka varsity", getString(R.string.dhaka_versity)));
        subAreaModelList.add(new SubAreaModel("Ramona", "New iskaton", getString(R.string.new_iskaton)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Naya Tola", getString(R.string.noya_tola)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Poribug", getString(R.string.poribug)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Bangla motor", getString(R.string.bangla_motor)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Beili road", getString(R.string.beili_road)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Boro mogbazar", getString(R.string.boro_mogbazar)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Moghbazar T & T colony", getString(R.string.mogbazar_tandt_colony)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Mirbug", getString(R.string.mirbug)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Shah bug", getString(R.string.shahbug)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Shiddessori", getString(R.string.shiddessori)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Segunbagicha", getString(R.string.segunbagicha)));
        subAreaModelList.add(new SubAreaModel("Ramona", "Hatirpul", getString(R.string.hatirpul)));


        subAreaModelList.add(new SubAreaModel("Lalbag", "Azimpur", getString(R.string.azimpur)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Amligola", getString(R.string.amligola)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Islam bug", getString(R.string.islambug)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Kamrangirchor", getString(R.string.kamrangirchor)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Chawkbazar", getString(R.string.chawkbazar)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Bakshi bazar", getString(R.string.bakshibazar)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Babu bazar", getString(R.string.babubazar)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Begum bazar", getString(R.string.begumbazar)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Lalbug", getString(R.string.lalbug)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Shahid nagar", getString(R.string.shahid_nagar)));
        subAreaModelList.add(new SubAreaModel("Lalbag", "Soyari ghat", getString(R.string.soyari_ghat)));


        subAreaModelList.add(new SubAreaModel("Shampur", "Kadamtali", getString(R.string.kadamtali)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Kutub khali", getString(R.string.kutub_khali)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Janata market", getString(R.string.janata_market)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Japani market", getString(R.string.japani_market)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Jurain", getString(R.string.jurain)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Doniya", getString(R.string.doniya)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Deul para", getString(R.string.deul_para)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Dholai para", getString(R.string.dholaipara)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Pagla", getString(R.string.pagla)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Faridabad", getString(R.string.faridabad)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Munshibug", getString(R.string.munshibug)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Muradpur", getString(R.string.muradpur)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Meraz nagar", getString(R.string.meraznagar)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Mohammad Bug", getString(R.string.mohammadbugg)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Rasulpur", getString(R.string.rasulpur)));
        subAreaModelList.add(new SubAreaModel("Shampur", "Rayer bug", getString(R.string.rayerbug)));


        subAreaModelList.add(new SubAreaModel("ShobujBug", "Ahmed Bug", getString(R.string.ahmedbug)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "North mugda para", getString(R.string.north_mugda_para)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Kadamtala", getString(R.string.kadamtala)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "South mugdapara", getString(R.string.south_mugdapara)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Natun para", getString(R.string.natunpara)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "East nandipara", getString(R.string.east_nandipara)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Baganbari", getString(R.string.baganbari)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Basabo", getString(R.string.basabo)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Madartech", getString(R.string.madartech)));
        subAreaModelList.add(new SubAreaModel("ShobujBug", "Mugdapara", getString(R.string.mugdapara)));

        subAreaModelList.add(new SubAreaModel("Sutrapur", "Wari", getString(R.string.wari)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Kaptan bazar", getString(R.string.kaptan_bazar)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Kerati tola", getString(R.string.keraati_tola)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Gendaria", getString(R.string.gendaria)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Tikatuli", getString(R.string.tikatuli)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Doyagang", getString(R.string.doyagang)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Dhup khola", getString(R.string.dhup_khola)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Nawabpur", getString(R.string.nawabpur)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Narinda", getString(R.string.narinda)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Jatrabari", getString(R.string.jatrabari)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Saydabad", getString(R.string.saydabad)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Soyami bug", getString(R.string.soyamibug)));
        subAreaModelList.add(new SubAreaModel("Sutrapur", "Hut khola", getString(R.string.hatkhola)));


        subAreaModelList.add(new SubAreaModel("Hajaribug", "Anayet Gang", getString(R.string.anayetgang)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Company ghat", getString(R.string.company_ghat)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Jigatola", getString(R.string.jigatola)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Tollabug", getString(R.string.tollabug)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Tinmazar", getString(R.string.tinmazar)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Nabab gang", getString(R.string.nababganj)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Pilkhana", getString(R.string.pilkhana)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Borhanpur", getString(R.string.borhanpur)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Monesshor", getString(R.string.monesshor)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Rayer bazar", getString(R.string.rayerbazar)));
        subAreaModelList.add(new SubAreaModel("Hajaribug", "Hazari bug", getString(R.string.hazaribug)));


        subAreaModelList.add(new SubAreaModel("Akborshah", "Alongkar mor", getString(R.string.alongkarmor)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Ishapani mor", getString(R.string.ishapanimor)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Ak khan", getString(R.string.akkhan)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Karnel hat", getString(R.string.karnelhat)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Kalir choura", getString(R.string.kalir_choura)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Kiobolo badh railway station", getString(R.string.kiobolo_badh_railway_station)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Josim market", getString(R.string.josim_market)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Pahartoli", getString(R.string.pahartoli)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Purobi feroz shah mazar", getString(R.string.purobi_feroz_shah_mazar)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "Firoz shah colony", getString(R.string.firoz_shah_colony)));
        subAreaModelList.add(new SubAreaModel("Akborshah", "CDA 1 no bus stop", getString(R.string.cda_1_no_bus_stop)));


        subAreaModelList.add(new SubAreaModel("Andharkilla", "Andhar killa", getString(R.string.andhar_killa)));


        subAreaModelList.add(new SubAreaModel("AmirbugAbashikalaka", "Amirbug Abashik alaka", getString(R.string.amirbug_abashik_alaka)));


        subAreaModelList.add(new SubAreaModel("Alishapara", "Ali shapara", getString(R.string.ali_shapara)));

        subAreaModelList.add(new SubAreaModel("Asadgonjcommercialarea", "Asadgonj commercial area", getString(R.string.asadganj_commercial_area)));


        subAreaModelList.add(new SubAreaModel("EPZ", "Ishan mistrir hat", getString(R.string.ishan_mistrir_hat)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Hritu hostel", getString(R.string.hritu_hostel)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Chowdhury bazar", getString(R.string.chowdhury_bazar)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Dhup pal", getString(R.string.dhup_pal)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Nim tola", getString(R.string.nim_tola)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Bandortila", getString(R.string.bandortila)));
        subAreaModelList.add(new SubAreaModel("EPZ", "BNA office", getString(R.string.bna_office)));
        subAreaModelList.add(new SubAreaModel("EPZ", "BNA upanibesh", getString(R.string.bna_upanibesh)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Bissho rasta", getString(R.string.bissho_rasta)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Lohar pul", getString(R.string.lohar_pul)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Saltgola", getString(R.string.saltgola)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Sagorika", getString(R.string.sagorika)));
        subAreaModelList.add(new SubAreaModel("EPZ", "CEPZ mailer matha", getString(R.string.cepz_mailer_matha)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Cement crossing", getString(R.string.cement_crossing)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Steel mil bazar", getString(R.string.steel_mil_bazar)));
        subAreaModelList.add(new SubAreaModel("EPZ", "Haspataler get", getString(R.string.haspataler_get)));
        subAreaModelList.add(new SubAreaModel("EPZ", "3 no fakir hat", getString(R.string._3_no_fakir_hat)));


        subAreaModelList.add(new SubAreaModel("Uttarpathantuli", "Uttar pathantuli", getString(R.string.uttar_pathantuli)));

        subAreaModelList.add(new SubAreaModel("Anayetbazar", "Anayet bazar", getString(R.string.anayet_bazar)));
        subAreaModelList.add(new SubAreaModel("KarnafuliResidentialarea", "Karnafuli Residential area", getString(R.string.karnafuli_residential_area)));


        subAreaModelList.add(new SubAreaModel("Kotoyali", "Amirbug", getString(R.string.amirbug)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Kajir deuri", getString(R.string.kajir_deuri)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Khatun ganj", getString(R.string.khatun_ganj)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Gani uponibesh", getString(R.string.gani_uponibesh)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Chattagram bandor", getString(R.string.chattagram_bandor)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Jaotola", getString(R.string.jaotola)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Firingi bazar", getString(R.string.firingi_bazar)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Pathorghat", getString(R.string.pathorghat)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Pathantuli", getString(R.string.pathantuli)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Station road", getString(R.string.station_road)));
        subAreaModelList.add(new SubAreaModel("Kotoyali", "Stand rasta", getString(R.string.stand_rasta)));

        subAreaModelList.add(new SubAreaModel("Khatungonj", "Khatungonj", getString(R.string.khatunganj)));


        subAreaModelList.add(new SubAreaModel("Khulshi", "Wireless mor", getString(R.string.wirelessmor)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Khulsi shahid", getString(R.string.khulsi_shahid)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Chattagram bandor nagori international university", getString(R.string.chattagram_bandor_nagori_international_university)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Chittagong Government Women's College", getString(R.string.chittagong_government_women_college)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Jautla Railway Station", getString(R.string.jautla_railway_station)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Polytechnic Institute", getString(R.string.polytechnic_institute)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Foyeslech", getString(R.string.foyeslech)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Bangladesh Agricultural Research Centre", getString(R.string.bangladesh_agricultural_research_centre)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "BGMEA", getString(R.string.bgmea)));
        subAreaModelList.add(new SubAreaModel("Khulshi", "Holy crescent bus stop", getString(R.string.holy_crescent_bus_stop)));

        subAreaModelList.add(new SubAreaModel("Gosaildanga", "Gosaildanga", getString(R.string.gosaildanga)));

        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Wasa mor", getString(R.string.wasa_mor)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Gani bekari mor", getString(R.string.gani_bekari_mor)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Chawk bazar bus stop", getString(R.string.chawk_bazar_bus_stop)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Chawk bazar super market", getString(R.string.chawk_bazar_super_market)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Chattessori mor", getString(R.string.chattessori_mor)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Jamal khan", getString(R.string.jamal_khan)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Deb pahar", getString(R.string.deb_pahar)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Perad moydan", getString(R.string.perad_moydan)));
        subAreaModelList.add(new SubAreaModel("Chowkbazar", "Boddo mondir", getString(R.string.boddo_mondir)));

        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Ekrampur Ispahani", getString(R.string.ekrampur_ispahani)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Kalgachiya", getString(R.string.kalgachiya)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Khaitkhali", getString(R.string.khaitkhali)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "TinGau", getString(R.string.tin_gau)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Boro Nayabazar", getString(R.string.boro_nayabazar)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Bag Nayaghar", getString(R.string.bag_nayaghar)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Madanapura Masjid", getString(R.string.madanapura_masjid)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Madanpur Khal", getString(R.string.madanpur_khal)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Madhav Pasha", getString(R.string.madhav_pasha)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Mahmud Nagar", getString(R.string.mahmud_nagar)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Rasulbagh", getString(R.string.rasulbagh)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Langalbrand", getString(R.string.langalbrand)));
        subAreaModelList.add(new SubAreaModel("Chattagrambondor", "Sonakanda", getString(R.string.sonakanda)));

        subAreaModelList.add(new SubAreaModel("Chorpara", "Chor para", getString(R.string.chor_para)));

        subAreaModelList.add(new SubAreaModel("Chorhalda", "Chor halda", getString(R.string.chor_halda)));

        subAreaModelList.add(new SubAreaModel("Chadgao", "Kaptai rastar matha", getString(R.string.kaptai_rastar_matha)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Kalurghat bus stop", getString(R.string.kalurghat_bus_stop)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Chattagram betar kendro", getString(R.string.chattragram_betar_kendro)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Chadgao abashik", getString(R.string.chadgao_abashik)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Bohoddarhat", getString(R.string.bohoddar_hat)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Bus terminal", getString(R.string.bus_terminal)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Bahir signal", getString(R.string.bahir_signal)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Moulvibazar", getString(R.string.moulvibazar)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "CNB", getString(R.string.cnb)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Haji Saber Ahmed Timber Company Limited", getString(R.string.haji_saber_ahmed_timber)));
        subAreaModelList.add(new SubAreaModel("Chadgao", "Hajera Taju degree college", getString(R.string.hajera_taju_degree_college)));

        subAreaModelList.add(new SubAreaModel("ChowdhuryPara", "Chowdhury Para", getString(R.string.chowdhury_para)));


        subAreaModelList.add(new SubAreaModel("GEMofficerscolony", "GEM officers colony", getString(R.string.gem_officers_colony)));
        subAreaModelList.add(new SubAreaModel("Jhawtola", "Jhawtola", getString(R.string.jhawtola)));
        subAreaModelList.add(new SubAreaModel("Tigerpassrailwaycolony", "Tigerpass railway colony", getString(R.string.tigerpass_railway_colony)));

        subAreaModelList.add(new SubAreaModel("TSPcolony", "TSP colony", getString(R.string.tsp_colony)));
        subAreaModelList.add(new SubAreaModel("Teribazar", "Teri bazar", getString(R.string.teri_bazar)));


        subAreaModelList.add(new SubAreaModel("Doublemuring", "Chattagram bondor", getString(R.string.chattagram_bondor)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Double muring", getString(R.string.double_muring)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "South agrabad", getString(R.string.south_agrabad)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Noya bazar pahartoli", getString(R.string.noyabazar_pahartoli)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Bou bazar", getString(R.string.bou_bazar)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Pahartoli", getString(R.string.pahartoli)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Bangladesh Bank", getString(R.string.bangladesh_bank_colony)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Bou bazar", getString(R.string.bou_bazar)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Mohuri paara", getString(R.string.mohuri_para)));
        subAreaModelList.add(new SubAreaModel("Doublemuring", "Soray para", getString(R.string.soray_para)));

        subAreaModelList.add(new SubAreaModel("Doijpara", "Doijpara", getString(R.string.doijpara)));
        subAreaModelList.add(new SubAreaModel("Southpatenga", "South patenga", getString(R.string.south_patenga)));
        subAreaModelList.add(new SubAreaModel("Southbondar", "South bondar", getString(R.string.south_bondor)));
        subAreaModelList.add(new SubAreaModel("Dokkhinmoddhoholyshohor", "Dokkhin moddho holyshohor", getString(R.string.dokkhin_moddho_holyshohor)));
        subAreaModelList.add(new SubAreaModel("Dampara", "Dampara", getString(R.string.dampara)));
        subAreaModelList.add(new SubAreaModel("Deoyanghat", "Deoyan ghat", getString(R.string.deyan_ghat)));
        subAreaModelList.add(new SubAreaModel("Dewanbazar", "Dewan bazar", getString(R.string.dewan_bazar)));
        subAreaModelList.add(new SubAreaModel("Dhumpara", "Dhumpara", getString(R.string.dhumpara)));
        subAreaModelList.add(new SubAreaModel("Northmiddleholishohor", "North middle holishohor", getString(R.string.north_middle_holishohor)));
        subAreaModelList.add(new SubAreaModel("Nasirabad", "Nasirabad", getString(R.string.nasirabad)));
        subAreaModelList.add(new SubAreaModel("Newmuring", "New muring", getString(R.string.new_muring)));
        subAreaModelList.add(new SubAreaModel("Navyport", "Navy port", getString(R.string.navy_port)));
        subAreaModelList.add(new SubAreaModel("Podmaabashikalaka", "Podma abashik alaka", getString(R.string.podma_abashik_alaka)));


        subAreaModelList.add(new SubAreaModel("Paslaish", "Aturar dipu", getString(R.string.aturar_dipu)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Amirbug R/A", getString(R.string.amirbug_r_a)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Chattroseri", getString(R.string.chattroseri)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "GEC mor", getString(R.string.gec_mor)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Pashlaish R/A", getString(R.string.pashlaish_ra)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Peyara bagan", getString(R.string.payara_bagan)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Bagmoniram", getString(R.string.bagmoniram)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "KhatMufijur rahman abashik alakaungonj", getString(R.string.mufijur_rahman_abashik_alaka)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Murad pur", getString(R.string.muradpur)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Medical staff quarter", getString(R.string.medical_staff_quarter)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Mehedi bug", getString(R.string.mehedibug)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Shulkobohor", getString(R.string.shulkobohor)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Sholoshohor", getString(R.string.sholoshohor)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Sholoshohor railway station", getString(R.string.sholoshorer_railway_station)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "Hamjarbag", getString(R.string.hamjarbug)));
        subAreaModelList.add(new SubAreaModel("Paslaish", "2 no. gate", getString(R.string._2_n0_gate)));

        subAreaModelList.add(new SubAreaModel("Pathantuli", "Pathantuli", getString(R.string.pathantuli)));


        subAreaModelList.add(new SubAreaModel("Potenga", "South port", getString(R.string.south_port)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Najira para", getString(R.string.najirapara)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Navy colony", getString(R.string.navy_colony)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Patenga beach", getString(R.string.patenga_beatch)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Porapara", getString(R.string.porapara)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Bangladesh navy golf club", getString(R.string.banglladesh_navy_golf_club)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Muslimabad", getString(R.string.muslimabad)));
        subAreaModelList.add(new SubAreaModel("Potenga", "Steel industries", getString(R.string.steel_industries)));


        subAreaModelList.add(new SubAreaModel("Patharghata", "Patharghata", getString(R.string.patharghata)));

        subAreaModelList.add(new SubAreaModel("Pahartali", "North katrali", getString(R.string.north_katrali)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Koibolo dam railway station", getString(R.string.koibolo_dam_railway_station)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Cricket stadium railway station", getString(R.string.cricket_stadium_railway_station)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "South katrali", getString(R.string.south_katrail)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Pahartoli railway station", getString(R.string.pahartoli_railway_station)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Firoz shah uponibesh", getString(R.string.firoz_shah_railway_station)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Bishorzo para", getString(R.string.bishorzopara)));
        subAreaModelList.add(new SubAreaModel("Pahartali", "Samoli abashik alaka", getString(R.string.samoli_abashik_alaka)));


        subAreaModelList.add(new SubAreaModel("Purbonimtala", "Purbo nimtala", getString(R.string.purbo_nimtala)));

        subAreaModelList.add(new SubAreaModel("Purbomadarbari", "Purbo madar bari", getString(R.string.purbo_madar_bari)));

        subAreaModelList.add(new SubAreaModel("Bahaddarhat", "Bahaddarhat", getString(R.string.bahaddarhat)));
        subAreaModelList.add(new SubAreaModel("Bangladeshbankcolony", "Bangladesh bank colony", getString(R.string.bangladesh_bank_colony)));

        subAreaModelList.add(new SubAreaModel("Bakolia", "Kalmia Bazar", getString(R.string.kalmia_bazar)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Khatunganj", getString(R.string.khatunganj)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Goni Bakeri More", getString(R.string.goni_bakerimore)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Dewan Bazar", getString(R.string.dewan_bazar)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Pathorghata", getString(R.string.pathorghata)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Bakshirhat", getString(R.string.bakshirhat)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Maizpara", getString(R.string.maizpara)));
        subAreaModelList.add(new SubAreaModel("Bakolia", "Rahattorpul", getString(R.string.rahattorpul)));

        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.oxygen_more)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.amin_jut_mile)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.arefin_nagor)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.ali_nagor)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.chittagong_cant_public_college)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.textile_gate)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.nobi_nagor)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.nasirabad)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.poly_technical)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.bangladesh_forest_research_institute_gate)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.bayazid_bostami)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.barma_coloni)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.rahaman_nagor)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.rawfabad)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.sher_shah_coloni)));
        subAreaModelList.add(new SubAreaModel("Bayazidbostami", "Bayazid bostami", getString(R.string.hamjarbag)));

        subAreaModelList.add(new SubAreaModel("Mansurabad", "Mansurabad", getString(R.string.mansurabad)));
        subAreaModelList.add(new SubAreaModel("Mansurabad", "Shafi Motors Limited", getString(R.string.shafi_motors_limited)));

        subAreaModelList.add(new SubAreaModel("Maijpara", "Maijpara", getString(R.string.maizpara)));
        subAreaModelList.add(new SubAreaModel("Rangiparabankcolony", "Rangipara bank colony", getString(R.string.rangipara_bank_colony)));
        subAreaModelList.add(new SubAreaModel("Laldairchar", "Laldair char", getString(R.string.laldair_char)));

        subAreaModelList.add(new SubAreaModel("Shadorghat", "Abhayamitra", getString(R.string.abhayamitra)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Karnaphuli Dockyard", getString(R.string.karnaphuli_dockyard)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Kamal Gate Bazar", getString(R.string.kamal_gate_bazar)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Choktai Khal", getString(R.string.choktai_khal)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Tin pooler matha", getString(R.string.tin_pool_matha)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Noton Bazar", getString(R.string.noton_bazar)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Bakshirhat", getString(R.string.baksirhat)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Motherbari Railway Pump Station", getString(R.string.motherbari_railway_pump_station)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Mia Khan Bridge", getString(R.string.mia_khan_setu)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Riaz Uddin Bazar", getString(R.string.riaz_uddin_bazar)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Laldigi more", getString(R.string.laldigi_more)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Sadarghat Jeti", getString(R.string.sadarghat_jt)));
        subAreaModelList.add(new SubAreaModel("Shadorghat", "Cinema Place", getString(R.string.cinema_place)));

        subAreaModelList.add(new SubAreaModel("Sondippara", "Sondip para", getString(R.string.sondip_para)));
        subAreaModelList.add(new SubAreaModel("Southagrabad", "KhatunSouth agrabadgonj", getString(R.string.south_agrabad)));
        subAreaModelList.add(new SubAreaModel("CGScolony", "CGS colony", getString(R.string.cgs_colony)));

        subAreaModelList.add(new SubAreaModel("Hali_shohor", "KNT Logistics Limited", getString(R.string.knt_logistics_limited)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Chittagong Container Terminal", getString(R.string.chittagong_container_terminal)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Chittagong Container Transportation Co. Ltd", getString(R.string.chittagong_container_tran_co_ltd)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Chittagong Bondor dharak", getString(R.string.chittagong_bondor_dharak)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Daksin Halishahar", getString(R.string.daksin_halishahar)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Noton sidebar", getString(R.string.noton_sidebar)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Newmooring Container Terminal", getString(R.string.newmooring_container_terminal)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Nau Bondor", getString(R.string.nau_bondor)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Bondor new mooring", getString(R.string.bondor_new_mooring)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Bondor link road", getString(R.string.bondor_link_road)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Munshiipara", getString(R.string.munshiipara)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Labor Colony", getString(R.string.labor_colony)));
        subAreaModelList.add(new SubAreaModel("Hali_shohor", "Halishahar Housing Society", getString(R.string.halishahar_housing_society)));

        subAreaModelList.add(new SubAreaModel("Halishohormunshipara", "Hali shohor munshipara", getString(R.string.hali_shohor_munshipara)));
        subAreaModelList.add(new SubAreaModel("Halishohorsenanibash", "Hali shohor senanibash", getString(R.string.hali_shohor_senanibash)));
        subAreaModelList.add(new SubAreaModel("Hosenahmedpara", "Hosen ahmedpara", getString(R.string.hosen_ahmedpara)));


        subAreaModelList.add(new SubAreaModel("Faridpur", "Alfadanga", getString(R.string.alfadanga)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Vanga", getString(R.string.vanga)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Boyalmari", getString(R.string.boyalmari)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Chorvodroson", getString(R.string.chorvodroson)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Faridpur sadar", getString(R.string.faridpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Madhukhali", getString(R.string.madhukhali)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Nagarkanda", getString(R.string.nagarkanda)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Sadarpur", getString(R.string.sadarpur)));
        subAreaModelList.add(new SubAreaModel("Faridpur", "Saltha", getString(R.string.saltha)));


        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Ostogram", getString(R.string.ostogram)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Bajitpur", getString(R.string.bajitpur)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Karimganj", getString(R.string.karimganj)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Bhairab", getString(R.string.bhairab)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Hosenpur", getString(R.string.hosenpur)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Itna", getString(R.string.itna)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Katiadi", getString(R.string.katiadi)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Kishoreganj Sadar", getString(R.string.kishoreganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Kuliyachor", getString(R.string.kuliyachor)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Mithamoin", getString(R.string.mithamoin)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Nikoli", getString(R.string.nikoli)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Pakundia", getString(R.string.pakundia)));
        subAreaModelList.add(new SubAreaModel("Kishorgonj", "Tarail", getString(R.string.tarail)));


        subAreaModelList.add(new SubAreaModel("Rajbari", "Baliakandi", getString(R.string.baliakandi)));
        subAreaModelList.add(new SubAreaModel("Rajbari", "Goyalondo", getString(R.string.goyalondo)));
        subAreaModelList.add(new SubAreaModel("Rajbari", "Kalukhali", getString(R.string.kalukhali)));
        subAreaModelList.add(new SubAreaModel("Rajbari", "Pangsha", getString(R.string.pangsha)));
        subAreaModelList.add(new SubAreaModel("Rajbari", "Rajbari Sadar", getString(R.string.rajbari_sadar)));


        subAreaModelList.add(new SubAreaModel("Tangail", "Basail", getString(R.string.basail)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Vuyapur", getString(R.string.vuyapur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Delduar", getString(R.string.delduar)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Dhanbari", getString(R.string.dhanbari)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Ghatail", getString(R.string.ghatail)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Gopalpur", getString(R.string.gopalpur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Kalihati", getString(R.string.kalihati)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Madhupur", getString(R.string.madhupur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Mirzapur", getString(R.string.mirzapur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Nagarpur", getString(R.string.nagarpur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Sakhipur", getString(R.string.sakhipur)));
        subAreaModelList.add(new SubAreaModel("Tangail", "Tangail Sadar", getString(R.string.tangail_sadar)));


        subAreaModelList.add(new SubAreaModel("Narsingdi", "Belabo", getString(R.string.belabo)));
        subAreaModelList.add(new SubAreaModel("Narsingdi", "Monohardi", getString(R.string.monohardi)));
        subAreaModelList.add(new SubAreaModel("Narsingdi", "Narsingdi sadar", getString(R.string.narsingdi_sadar)));
        subAreaModelList.add(new SubAreaModel("Narsingdi", "Polash", getString(R.string.polash)));
        subAreaModelList.add(new SubAreaModel("Narsingdi", "Raipura", getString(R.string.raipura)));
        subAreaModelList.add(new SubAreaModel("Narsingdi", "Shibpur", getString(R.string.shibpur)));


        subAreaModelList.add(new SubAreaModel("Shariatpur", "Vedorganj", getString(R.string.vedorganj)));
        subAreaModelList.add(new SubAreaModel("Shariatpur", "Damuda", getString(R.string.demuda)));
        subAreaModelList.add(new SubAreaModel("Shariatpur", "Gosairhat", getString(R.string.gosairhat)));
        subAreaModelList.add(new SubAreaModel("Shariatpur", "Noriya", getString(R.string.noriya)));
        subAreaModelList.add(new SubAreaModel("Shariatpur", "Shariatpur Sadar", getString(R.string.shariatpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Shariatpur", "Jajira", getString(R.string.jajira)));


        subAreaModelList.add(new SubAreaModel("Dhaka", "Dhamrai", getString(R.string.dhamrai)));
        subAreaModelList.add(new SubAreaModel("Dhaka", "Dohar", getString(R.string.dohar)));
        subAreaModelList.add(new SubAreaModel("Dhaka", "Keraniganj", getString(R.string.keraniganj)));
        subAreaModelList.add(new SubAreaModel("Dhaka", "Nobabganj", getString(R.string.nobabganj)));
        subAreaModelList.add(new SubAreaModel("Dhaka", "Savar", getString(R.string.savar)));

        subAreaModelList.add(new SubAreaModel("Manikgonj", "Doulatpur", getString(R.string.doulotpur)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Ghior", getString(R.string.ghior)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Harirampur", getString(R.string.harirampur)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Manikganj sadar", getString(R.string.manikganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Saturia", getString(R.string.saturia)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Shibaloy", getString(R.string.shibloy)));
        subAreaModelList.add(new SubAreaModel("Manikgonj", "Singair", getString(R.string.singair)));


        subAreaModelList.add(new SubAreaModel("Munshigonj", "Gozaria", getString(R.string.gozaria)));
        subAreaModelList.add(new SubAreaModel("Munshigonj", "Louhajong", getString(R.string.louhajong)));
        subAreaModelList.add(new SubAreaModel("Munshigonj", "Munshiganj sadar", getString(R.string.munshiganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Munshigonj", "Srinagar", getString(R.string.srinagar)));
        subAreaModelList.add(new SubAreaModel("Munshigonj", "Sirajdikhan", getString(R.string.sirajdikhan)));
        subAreaModelList.add(new SubAreaModel("Munshigonj", "Tongibari", getString(R.string.tongibari)));


        subAreaModelList.add(new SubAreaModel("Gopalgonj", "Gopalganj sadar", getString(R.string.gopalganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Gopalgonj", "Kashiyani", getString(R.string.kashiyani)));
        subAreaModelList.add(new SubAreaModel("Gopalgonj", "Kotalipara", getString(R.string.kotalipara)));
        subAreaModelList.add(new SubAreaModel("Gopalgonj", "Muksudpur", getString(R.string.muksudpur)));
        subAreaModelList.add(new SubAreaModel("Gopalgonj", "Tungipara", getString(R.string.tungipara)));


        subAreaModelList.add(new SubAreaModel("Madaripur", "Kalkini", getString(R.string.kalkini)));
        subAreaModelList.add(new SubAreaModel("Madaripur", "Dasar", getString(R.string.dasar)));
        subAreaModelList.add(new SubAreaModel("Madaripur", "Madaripur sadar", getString(R.string.madaripur_sadar)));
        subAreaModelList.add(new SubAreaModel("Madaripur", "Rajoub", getString(R.string.rajoiub)));
        subAreaModelList.add(new SubAreaModel("Madaripur", "Shibchar", getString(R.string.shibchar)));


        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Araihazar", getString(R.string.araihazar)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Bandar", getString(R.string.bandar)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Narayanganj sadar", getString(R.string.narayanganj_sadar)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Narayanganj city", getString(R.string.narayanganj_city)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Rupganj", getString(R.string.rupganj)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Sonargaon", getString(R.string.sonargoan)));
        subAreaModelList.add(new SubAreaModel("NarayanGanj", "Fatullah", getString(R.string.fatullah)));


        subAreaModelList.add(new SubAreaModel("Gazipur", "Kaliganj", getString(R.string.kaliganj)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Kaliakoir", getString(R.string.kaliakoir)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Kapasia", getString(R.string.kapasia)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Basan", getString(R.string.basan)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Gazipur sadar", getString(R.string.gazipur_sadar)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Gazipur city corporation", getString(R.string.gazipur_city_corporation)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Sripur", getString(R.string.sripur)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Kayaloti", getString(R.string.kayaloti)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Konabari", getString(R.string.konabari)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Gasa", getString(R.string.gasa)));
        subAreaModelList.add(new SubAreaModel("Gazipur", "Kashimpur", getString(R.string.kashimpur)));


        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Bancharampur", getString(R.string.bancharampur)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Bijoynagar", getString(R.string.bijoynagar)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Akhaura", getString(R.string.akhaura)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Ashugonj", getString(R.string.ashuganj)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Kosba", getString(R.string.kosba)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Nobinogor", getString(R.string.nabinagar)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Nasirnogor", getString(R.string.nasirnagar)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Brahmanbaria sadar", getString(R.string.brahmanbaria_sadar)));
        subAreaModelList.add(new SubAreaModel("Brahmanbaria", "Sarail", getString(R.string.sarail)));


        subAreaModelList.add(new SubAreaModel("Bandarban", "Alikodom", getString(R.string.alikodom)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Thanchi", getString(R.string.thanchi)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Naikkhongchori", getString(R.string.naikkhonchori)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Ruma", getString(R.string.ruma)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Bandarban sadar", getString(R.string.bandarban_sadar)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Rongchori", getString(R.string.rongchori)));
        subAreaModelList.add(new SubAreaModel("Bandarban", "Lama", getString(R.string.lama)));


        subAreaModelList.add(new SubAreaModel("Chittagong", "Anoyara", getString(R.string.anoyara)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Chondonaish", getString(R.string.chondonaish)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Bashkhali", getString(R.string.bashkhali)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Boalkhali", getString(R.string.boalkhali)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Mirsarai", getString(R.string.mirsarai)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Sondip", getString(R.string.sondip)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Satkania", getString(R.string.satkania)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Hathazari", getString(R.string.hathazari)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Kornofuli", getString(R.string.kornofuli)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Potiya", getString(R.string.potiya)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Fotikchori", getString(R.string.fotikchori)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Roujan", getString(R.string.roujan)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Rangunia", getString(R.string.rangunia)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Lohagara", getString(R.string.hohagara)));
        subAreaModelList.add(new SubAreaModel("Chittagong", "Sitakunda", getString(R.string.sitakunda)));


        subAreaModelList.add(new SubAreaModel("Rangamati", "Kaptai", getString(R.string.kaptai)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Kaukhali", getString(R.string.kaukhali)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Jurachori", getString(R.string.jurachri)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Naniarchar", getString(R.string.naniarchar)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Borkol", getString(R.string.borkol)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Bagaichori", getString(R.string.bagaichori)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Bilaichori", getString(R.string.bilaichori)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Rangamati sadar", getString(R.string.rangamati_sadar)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Rajstoli", getString(R.string.rajstoli)));
        subAreaModelList.add(new SubAreaModel("Rangamati", "Longgodu", getString(R.string.longodu)));


        subAreaModelList.add(new SubAreaModel("Comilla", "Nangalkot", getString(R.string.nangalkot)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Burichong", getString(R.string.burichong)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Muradnagar", getString(R.string.muradnagar)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Comilla city", getString(R.string.comilla_city)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Comilla sadar", getString(R.string.comilla_sadar)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Chandina", getString(R.string.chandina)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Choddogram", getString(R.string.choddogram)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Titas", getString(R.string.titas)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Debidar", getString(R.string.debidar)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Daudkandi", getString(R.string.daudkandi)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Borura", getString(R.string.borura)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Brahmanpara", getString(R.string.brahmanpara)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Monohorgonj", getString(R.string.monohorganj)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Megna", getString(R.string.megna)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Laksham", getString(R.string.laksam)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Lalmai", getString(R.string.lalmai)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Sadar dokkhin", getString(R.string.sadar_dokkhin)));
        subAreaModelList.add(new SubAreaModel("Comilla", "Homna", getString(R.string.homna)));


        subAreaModelList.add(new SubAreaModel("Noakhali", "Kabirhat", getString(R.string.kabirhat)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Kompanigonj", getString(R.string.kompaniganj)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Chatkhil", getString(R.string.chatkhil)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Noakhali sadar", getString(R.string.noakhali_sadar)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Begumgonj", getString(R.string.begumganj)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Subornochor", getString(R.string.subornochor)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Senbag", getString(R.string.senbug)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Sonaimuri", getString(R.string.sonaimuri)));
        subAreaModelList.add(new SubAreaModel("Noakhali", "Hatiya", getString(R.string.hatiya)));


        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Ukhiya", getString(R.string.ukhiya)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Kutubdia", getString(R.string.kutubdia)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Cox's Bazar sadar", getString(R.string.coxsbazar_sadar)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Chokoria", getString(R.string.chokoria)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Teknaf", getString(R.string.teknaf)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Pekuya", getString(R.string.pekuya)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Moheshkhali", getString(R.string.moheshkhali)));
        subAreaModelList.add(new SubAreaModel("CoxsBazar", "Ramu", getString(R.string.ramu)));


        subAreaModelList.add(new SubAreaModel("Chandpur", "Kochuya", getString(R.string.kochuya)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Motlob dokkhain", getString(R.string.motlob_dokkhin)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Chandpur sadar", getString(R.string.chandpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Faridgonj", getString(R.string.faridganj)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Motlob uttar", getString(R.string.motlob_sadar)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Shahrasti", getString(R.string.shahrasti)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Haimchor", getString(R.string.hhaimchor)));
        subAreaModelList.add(new SubAreaModel("Chandpur", "Hajigonj", getString(R.string.hajiganj)));


        subAreaModelList.add(new SubAreaModel("Feni", "Dagonbhuiyan", getString(R.string.dagunbhuiyan)));
        subAreaModelList.add(new SubAreaModel("Feni", "Chagla naiya", getString(R.string.changla_naiya)));
        subAreaModelList.add(new SubAreaModel("Feni", "Porshuram", getString(R.string.porshuram)));
        subAreaModelList.add(new SubAreaModel("Feni", "Fulgazi", getString(R.string.fulgazi)));
        subAreaModelList.add(new SubAreaModel("Feni", "Feni sadar", getString(R.string.feni_sadar)));
        subAreaModelList.add(new SubAreaModel("Feni", "Sonagazi", getString(R.string.sonagazi)));


        subAreaModelList.add(new SubAreaModel("Khagrasori", "Guimara", getString(R.string.guimara)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Matiranga", getString(R.string.matiranga)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Manikchari", getString(R.string.manikchari)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Ramgarh", getString(R.string.ramgarh)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Khagrachari sadar", getString(R.string.khagrachari_sadar)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Dighinala", getString(R.string.dighinala)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Panchori", getString(R.string.panchori)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Mohalchori", getString(R.string.mohalchori)));
        subAreaModelList.add(new SubAreaModel("Khagrasori", "Lokkhichori", getString(R.string.lokkhichori)));


        subAreaModelList.add(new SubAreaModel("Lokkhipur", "Komol Nogor", getString(R.string.komol_nagar)));
        subAreaModelList.add(new SubAreaModel("Lokkhipur", "Ramgoti", getString(R.string.ramgoti)));
        subAreaModelList.add(new SubAreaModel("Lokkhipur", "Raipur", getString(R.string.raipur)));
        subAreaModelList.add(new SubAreaModel("Lokkhipur", "Ramgonj", getString(R.string.ramganj)));
        subAreaModelList.add(new SubAreaModel("Lokkhipur", "Lakshmipur sadar", getString(R.string.laksmipur_sadar)));


        subAreaModelList.add(new SubAreaModel("Barisal", "Agoilojhara", getString(R.string.agoilojhara)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Babuganj", getString(R.string.babuganj)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Bakerganj", getString(R.string.bakerganj)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Banaripara", getString(R.string.banaripara)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Barisal sadar", getString(R.string.barishal_sadar)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Gournadi", getString(R.string.gournadi)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Mehendiganj", getString(R.string.mehendiganj)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Muladi", getString(R.string.muladi)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Hijla", getString(R.string.hijla)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Ujirpur", getString(R.string.ujirpur)));
        subAreaModelList.add(new SubAreaModel("Barisal", "Barisal city", getString(R.string.barisal_city)));


        subAreaModelList.add(new SubAreaModel("Barguna", "Amtali", getString(R.string.amtali)));
        subAreaModelList.add(new SubAreaModel("Barguna", "Bamna", getString(R.string.bamna)));
        subAreaModelList.add(new SubAreaModel("Barguna", "Barguna sadar", getString(R.string.barguna_sadar)));
        subAreaModelList.add(new SubAreaModel("Barguna", "Betagi", getString(R.string.betagi)));
        subAreaModelList.add(new SubAreaModel("Barguna", "Patharghata", getString(R.string.patharghata)));
        subAreaModelList.add(new SubAreaModel("Barguna", "Taltoli", getString(R.string.taltoli)));


        subAreaModelList.add(new SubAreaModel("Patuakhali", "Baufal", getString(R.string.baufal)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Doshmina", getString(R.string.doshmina)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Dumki", getString(R.string.dumki)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Galachipa", getString(R.string.galachipa)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Kalapara", getString(R.string.kalapara)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Mirzaganj", getString(R.string.mirzaganj)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Patuakhali sadar", getString(R.string.patuakhali_sadar)));
        subAreaModelList.add(new SubAreaModel("Patuakhali", "Rangabali", getString(R.string.rangabali)));


        subAreaModelList.add(new SubAreaModel("Pirojpur", "Vandariya", getString(R.string.vandariya)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Kaukhali", getString(R.string.kaukhali)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Motbariya", getString(R.string.motbariya)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Najirpur", getString(R.string.najirpur)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Nesarabad", getString(R.string.nesarabad)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Pirojpur Sadar", getString(R.string.pirojpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Pirojpur", "Jiyanagar", getString(R.string.jiyanagar)));


        subAreaModelList.add(new SubAreaModel("Bhola", "Bhola sadar", getString(R.string.bhola_sadar)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Borhanuddin", getString(R.string.borhanuddn)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Charfashion", getString(R.string.charfashion)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Doulathkhan", getString(R.string.doulatkhan)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Lalmohan", getString(R.string.lalmohan)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Monpura", getString(R.string.monpura)));
        subAreaModelList.add(new SubAreaModel("Bhola", "Tajumuddin", getString(R.string.tajumuddin)));


        subAreaModelList.add(new SubAreaModel("Jhalokati", "Jhalokati sadar", getString(R.string.jhalokati_sadar)));
        subAreaModelList.add(new SubAreaModel("Jhalokati", "Kathaliya", getString(R.string.kathaliya)));
        subAreaModelList.add(new SubAreaModel("Jhalokati", "Nolsity", getString(R.string.nolsity)));
        subAreaModelList.add(new SubAreaModel("Jhalokati", "Rajapur", getString(R.string.rajapur)));


        subAreaModelList.add(new SubAreaModel("Jessore", "Avoynagar", getString(R.string.avoynagar)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Bagharpara", getString(R.string.bagharpara)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Chougasa", getString(R.string.chougasa)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Jessore sadar", getString(R.string.jessore_sadar)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Jhikargacha", getString(R.string.jhikargacha)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Keshabpur", getString(R.string.keshabpur)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Monirampur", getString(R.string.monirampur)));
        subAreaModelList.add(new SubAreaModel("Jessore", "Sharsha", getString(R.string.sharsha)));


        subAreaModelList.add(new SubAreaModel("Chuadanga", "Alamdanga", getString(R.string.alamdanga)));
        subAreaModelList.add(new SubAreaModel("Chuadanga", "Chuadanga sadar", getString(R.string.chuadanga_sadar)));
        subAreaModelList.add(new SubAreaModel("Chuadanga", "Damurhuda", getString(R.string.damurhuda)));
        subAreaModelList.add(new SubAreaModel("Chuadanga", "Jibonnagar", getString(R.string.jibonnagar)));

        subAreaModelList.add(new SubAreaModel("Satkhira", "Ashasuni", getString(R.string.ashasuni)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Debhata", getString(R.string.debhata)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Kolaroya", getString(R.string.kolaraya)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Kaliganj", getString(R.string.kaliganj)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Satkhira sadar", getString(R.string.satkhira_sadar)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Shyamnagar", getString(R.string.shyamnagar)));
        subAreaModelList.add(new SubAreaModel("Satkhira", "Tala", getString(R.string.tala)));


        subAreaModelList.add(new SubAreaModel("Bagerhat", "Bagerhat sadar", getString(R.string.bagherhat_sadar)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Citolmari", getString(R.string.citolmari)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Fakirhat", getString(R.string.fakirhat)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Kocuya", getString(R.string.kochuya)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Mollahat", getString(R.string.mollarhat)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Mongla", getString(R.string.mongla)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Morolganj", getString(R.string.morolganj)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Rampal", getString(R.string.rampal)));
        subAreaModelList.add(new SubAreaModel("Bagerhat", "Shoronkhola", getString(R.string.shoronkhola)));


        subAreaModelList.add(new SubAreaModel("Kustia", "Veramara", getString(R.string.veramara)));
        subAreaModelList.add(new SubAreaModel("Kustia", "Doulatpur", getString(R.string.doulatpur)));
        subAreaModelList.add(new SubAreaModel("Kustia", "Khoksa", getString(R.string.khoksa)));
        subAreaModelList.add(new SubAreaModel("Kustia", "Kumarkhali", getString(R.string.kumarkhali)));
        subAreaModelList.add(new SubAreaModel("Kustia", "Kushtia sadar", getString(R.string.kustia_sadar)));
        subAreaModelList.add(new SubAreaModel("Kustia", "Mirpur kushtia", getString(R.string.mirpur_kustia)));


        subAreaModelList.add(new SubAreaModel("Khulna", "Batiaghata", getString(R.string.batiaghata)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Dakop", getString(R.string.dakop)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Dhigliya", getString(R.string.dhigliya)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Dumuriya", getString(R.string.dumuriya)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Fultola", getString(R.string.fultola)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Koyra", getString(R.string.koyra)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Paikgasa", getString(R.string.paikgasa)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Rupsa", getString(R.string.rupsa)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Terokhada", getString(R.string.terokhada)));
        subAreaModelList.add(new SubAreaModel("Khulna", "Khulna city", getString(R.string.khulna_city)));


        subAreaModelList.add(new SubAreaModel("Meherpur", "Gangni", getString(R.string.gangni)));
        subAreaModelList.add(new SubAreaModel("Meherpur", "Meherpur sadar", getString(R.string.meherpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Meherpur", "Mujibnagar", getString(R.string.mujibnagar)));


        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Harinakundu", getString(R.string.harinakundu)));
        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Jhenaidah sadar", getString(R.string.jhenaidah_sadar)));
        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Kaliganj", getString(R.string.kaliganj)));
        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Kotchandpur", getString(R.string.kotchandpur)));
        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Maheshpur", getString(R.string.maheshpur)));
        subAreaModelList.add(new SubAreaModel("Jhenaidah", "Shailkupa", getString(R.string.shailkupa)));


        subAreaModelList.add(new SubAreaModel("Norail", "Kaliya", getString(R.string.kaliya)));
        subAreaModelList.add(new SubAreaModel("Norail", "Lohagara", getString(R.string.lohagara)));
        subAreaModelList.add(new SubAreaModel("Norail", "Narail sadar", getString(R.string.narail_sadar)));


        subAreaModelList.add(new SubAreaModel("Magura", "Magura sadar", getString(R.string.magura_sadar)));
        subAreaModelList.add(new SubAreaModel("Magura", "Mohammadpur", getString(R.string.mohammadpur)));
        subAreaModelList.add(new SubAreaModel("Magura", "Shalikha", getString(R.string.shalikha)));
        subAreaModelList.add(new SubAreaModel("Magura", "Sripur", getString(R.string.sripur)));


        subAreaModelList.add(new SubAreaModel("Lalmonir hat", "Aditmari", getString(R.string.aditmari)));
        subAreaModelList.add(new SubAreaModel("Lalmonir hat", "Hatibandha", getString(R.string.hatibandha)));
        subAreaModelList.add(new SubAreaModel("Lalmonir hat", "Kaliganj", getString(R.string.kaliganj)));
        subAreaModelList.add(new SubAreaModel("Lalmonir hat", "Patgram", getString(R.string.patgram)));
        subAreaModelList.add(new SubAreaModel("Lalmonir hat", "Lalmonirhat sadar", getString(R.string.lalmonirhat_sadar)));


        subAreaModelList.add(new SubAreaModel("Ponchogor", "Atoyari", getString(R.string.atoyari)));
        subAreaModelList.add(new SubAreaModel("Ponchogor", "Boda", getString(R.string.boda)));
        subAreaModelList.add(new SubAreaModel("Ponchogor", "Debiganj", getString(R.string.debiganj)));
        subAreaModelList.add(new SubAreaModel("Ponchogor", "Panchagar", getString(R.string.panchagar)));
        subAreaModelList.add(new SubAreaModel("Ponchogor", "tetulia", getString(R.string.tetulia)));


        subAreaModelList.add(new SubAreaModel("Rangpur", "Badarganj", getString(R.string.badarganj)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Gangachara", getString(R.string.gangachar)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Kaunia", getString(R.string.kaunia)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Mithapukur", getString(R.string.mithapukur)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Pirgasa", getString(R.string.pirgasa)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Pirganj", getString(R.string.pirganj)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Rangpur sadar", getString(R.string.rangpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Taraganj", getString(R.string.taraganj)));
        subAreaModelList.add(new SubAreaModel("Rangpur", "Rangpur city", getString(R.string.rangpur_city)));


        subAreaModelList.add(new SubAreaModel("Thakurgaon", "Baliya Dangi", getString(R.string.baliya_dangi)));
        subAreaModelList.add(new SubAreaModel("Thakurgaon", "Haripur", getString(R.string.haripur)));
        subAreaModelList.add(new SubAreaModel("Thakurgaon", "Pirganj", getString(R.string.pirganj)));
        subAreaModelList.add(new SubAreaModel("Thakurgaon", "Ranisankail", getString(R.string.ranisankail)));
        subAreaModelList.add(new SubAreaModel("Thakurgaon", "Thakurgaon sadar", getString(R.string.thakurgaon_sadar)));


        subAreaModelList.add(new SubAreaModel("Kurigram", "Bhurungamari", getString(R.string.bhurangamari)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Char rajibpur", getString(R.string.char_rajibpur)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Chilmari", getString(R.string.chilmari)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Kaliganj", getString(R.string.kaliganj)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Kurigram sadar", getString(R.string.kurigram_sadar)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Nageswari", getString(R.string.nageswari)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Fulbari", getString(R.string.fulbari)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Rajarhat", getString(R.string.rajarhat)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Roumari", getString(R.string.roumari)));
        subAreaModelList.add(new SubAreaModel("Kurigram", "Ulipur", getString(R.string.ulipur)));


        subAreaModelList.add(new SubAreaModel("Dinajpur", "Birampur", getString(R.string.birampur)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Birganj", getString(R.string.birganj)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Bochaganj", getString(R.string.bochaganj)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Birol", getString(R.string.birol)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Chirirbandar", getString(R.string.chirirbandar)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Dinajpur sadar", getString(R.string.dinajpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Fulbari", getString(R.string.fulbari)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Ghoraghat", getString(R.string.ghorahat)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Hakimpur", getString(R.string.hakimpur)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Kaharol", getString(R.string.kaharol)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Khansama", getString(R.string.khansama)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Nababganj", getString(R.string.nababganj)));
        subAreaModelList.add(new SubAreaModel("Dinajpur", "Parbatipur", getString(R.string.parbatipur)));


        subAreaModelList.add(new SubAreaModel("Nilfamari", "Dimla", getString(R.string.dimla)));
        subAreaModelList.add(new SubAreaModel("Nilfamari", "Domar", getString(R.string.domar)));
        subAreaModelList.add(new SubAreaModel("Nilfamari", "Jaldhaka", getString(R.string.jaldhaka)));
        subAreaModelList.add(new SubAreaModel("Nilfamari", "Kishorgonj", getString(R.string.kishorgonj)));
        subAreaModelList.add(new SubAreaModel("Nilfamari", "Nilphamari sadar", getString(R.string.nilphamari_sadar)));
        subAreaModelList.add(new SubAreaModel("Nilfamari", "Saidpur", getString(R.string.saidpur)));


        subAreaModelList.add(new SubAreaModel("Gaibandha", "Gaibandha sadar", getString(R.string.gaibandha_sadar)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Gobindaganj", getString(R.string.gobindaganj)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Palashbari", getString(R.string.palashbari)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Fulsori", getString(R.string.fulsori)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Sadullahpur", getString(R.string.sadullahpur)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Saghata", getString(R.string.saghata)));
        subAreaModelList.add(new SubAreaModel("Gaibandha", "Sundorganj", getString(R.string.sundorganj)));


        subAreaModelList.add(new SubAreaModel("Bagura", "Bogra Sadar", getString(R.string.bogra_sadar)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Gabtoli", getString(R.string.gabtoli)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Sariakandi", getString(R.string.sariakandi)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Adamdighi", getString(R.string.adamdighi)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Sonatala", getString(R.string.sonatala)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Sherpur", getString(R.string.sherpur)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Kahaloo", getString(R.string.kahaloo)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Shibganj", getString(R.string.shibganj)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Dupchanchia", getString(R.string.dupchachia)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Nandigram", getString(R.string.nandigram)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Sahajanpur", getString(R.string.sahajahanpur)));
        subAreaModelList.add(new SubAreaModel("Bagura", "Dhunat", getString(R.string.dhunat)));


        subAreaModelList.add(new SubAreaModel("Chapainawabganj", "Gomastapur", getString(R.string.gomastapur)));
        subAreaModelList.add(new SubAreaModel("Chapainawabganj", "Chapainawabganj Sadar", getString(R.string.chapainawabganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Chapainawabganj", "Nachole", getString(R.string.nachole)));
        subAreaModelList.add(new SubAreaModel("Chapainawabganj", "Bholahat", getString(R.string.bholahat)));
        subAreaModelList.add(new SubAreaModel("Chapainawabganj", "Shibganj", getString(R.string.shibganj)));


        subAreaModelList.add(new SubAreaModel("Joypurhat", "Akkelpur", getString(R.string.akkelpur)));
        subAreaModelList.add(new SubAreaModel("Joypurhat", "Kalai", getString(R.string.kalai)));
        subAreaModelList.add(new SubAreaModel("Joypurhat", "Khetlal", getString(R.string.khetlal)));
        subAreaModelList.add(new SubAreaModel("Joypurhat", "Joypurhat Sadar", getString(R.string.joypurhat_sadar)));
        subAreaModelList.add(new SubAreaModel("Joypurhat", "Panchbibi", getString(R.string.panchbibi)));


        subAreaModelList.add(new SubAreaModel("Nouga", "Atrai", getString(R.string.atrai)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Dhamoirhat", getString(R.string.dhamoirhat)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Niamatpur", getString(R.string.niamatpur)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Patnitala", getString(R.string.patnitala)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Porsha", getString(R.string.porsha)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Badalgachhi", getString(R.string.badalgachhi)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Mahadebpur", getString(R.string.mahadebpur)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Manda", getString(R.string.manda)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Naogaon Sadar", getString(R.string.naogaon_sadar)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Raninagar", getString(R.string.raninagar)));
        subAreaModelList.add(new SubAreaModel("Nouga", "Sapahar", getString(R.string.sapahar)));


        subAreaModelList.add(new SubAreaModel("Natore", "Gurudaspur", getString(R.string.gurudaspur)));
        subAreaModelList.add(new SubAreaModel("Natore", "Naldanga", getString(R.string.naldanga)));
        subAreaModelList.add(new SubAreaModel("Natore", "Natore Sadar", getString(R.string.natore_sadar)));
        subAreaModelList.add(new SubAreaModel("Natore", "Baraigram", getString(R.string.baraigram)));
        subAreaModelList.add(new SubAreaModel("Natore", "Bagatipara", getString(R.string.bagatipara)));
        subAreaModelList.add(new SubAreaModel("Natore", "Lalpur", getString(R.string.lalpur)));
        subAreaModelList.add(new SubAreaModel("Natore", "Singra", getString(R.string.singra)));


        subAreaModelList.add(new SubAreaModel("Pabna", "Bera",getString(R.string.bera) ));
        subAreaModelList.add(new SubAreaModel("Pabna", "Bhangura", getString(R.string.bhangura)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Chatmohar", getString(R.string.chatmohar)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Ishwardi", getString(R.string.Ishwardi)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Pabna Sadar", getString(R.string.Pabna_Sadar)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Sathia", getString(R.string.Sathia)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Sujanagar", getString(R.string.Sujanagar)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Atghoria", getString(R.string.Atghoria)));
        subAreaModelList.add(new SubAreaModel("Pabna", "Faridpur", getString(R.string.faridpur)));


        subAreaModelList.add(new SubAreaModel("Rajshahi", "Durgapur", getString(R.string.Durgapur)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Bagha", getString(R.string.Bagha)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Bagmara", getString(R.string.Bagmara)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Charghat", getString(R.string.Charghat)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Godagari", getString(R.string.Godagari)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Mohonpur", getString(R.string.Mohonpur)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Paba", getString(R.string.Paba)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Puthia", getString(R.string.Puthia)));
        subAreaModelList.add(new SubAreaModel("Rajshahi", "Tanore", getString(R.string.Tanore)));


        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Belkuchi", getString(R.string.Belkuchi)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Chauhali", getString(R.string.Chauhali)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Kamarkhanda", getString(R.string.Kamarkhanda)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Kazipur", getString(R.string.Kazipur)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Raiganj", getString(R.string.Raiganj)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Shahjadpur", getString(R.string.Shahjadpur)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Sirajganj sadar", getString(R.string.Sirajganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Tarash", getString(R.string.Tarash)));
        subAreaModelList.add(new SubAreaModel("Sirajgonj", "Ullapara", getString(R.string.Ullapara)));


        subAreaModelList.add(new SubAreaModel("Habiganj", "Ajmiriganj", getString(R.string.ajmiriganj)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Bahubal", getString(R.string.bahubal)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Baniachong", getString(R.string.baniachong)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Chunarughat", getString(R.string.chunarughat)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Habiganj sadar", getString(R.string.habiganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Lakhai", getString(R.string.lakhai)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Madhabpur", getString(R.string.madhabpur)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Nabiganj", getString(R.string.nabiganj)));
        subAreaModelList.add(new SubAreaModel("Habiganj", "Shaistaganj", getString(R.string.shaistaganj)));


        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Barlekha", getString(R.string.barlekha)));
        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Juri", getString(R.string.juri)));
        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Kamalganj", getString(R.string.kamalganj)));
        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Kulaura", getString(R.string.kulaura)));
        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Moulvibazar sadar", getString(R.string.moulvibazar_sadar)));
        subAreaModelList.add(new SubAreaModel("Moulvibazar", "Srimangal", getString(R.string.srimangal)));


        subAreaModelList.add(new SubAreaModel("Sylhet", "Belal Ganj", getString(R.string.belalganj)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Biyani Bazar", getString(R.string.biyanibazar)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Bishwanath", getString(R.string.bishwanath)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Companiganj", getString(R.string.companiganj)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Dokkhin surma", getString(R.string.dokkhin_surma)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Fenchuganj", getString(R.string.fenchuganj)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Gopalganj", getString(R.string.gopalganj)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Goyainghat", getString(R.string.goyainghat)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Jointapur", getString(R.string.jointapur)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Jokiganj", getString(R.string.jokiganj)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Kanaighat", getString(R.string.kanaighat)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Osmani nagar", getString(R.string.osmaninagar)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Sylhet sadar", getString(R.string.sylhet_sadar)));
        subAreaModelList.add(new SubAreaModel("Sylhet", "Sylhet city", getString(R.string.sylhet_city)));


        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Bissomvorpur", getString(R.string.bissomvorpur)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Satok", getString(R.string.satok)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Derai", getString(R.string.derai)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Dharmapasha", getString(R.string.dharmapasha)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Doyarabazar", getString(R.string.doyarabazar)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Jagannathpur", getString(R.string.jagannathpur)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Jamalganj", getString(R.string.jamalganj)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Salla", getString(R.string.salla)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Madhyanagar", getString(R.string.madhyanagar)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Sunamganj sadar", getString(R.string.sunamganj_sadar)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Dokkhin sunamganj", getString(R.string.dokkhin_sunamganj)));
        subAreaModelList.add(new SubAreaModel("Sunamgonj", "Tahirpur", getString(R.string.tahirpur)));


        subAreaModelList.add(new SubAreaModel("Netrokona", "Atpara", getString(R.string.atpara)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Barohatta", getString(R.string.barohatta)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Durgapur", getString(R.string.durgapur)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Komolakanter", getString(R.string.komolakanter)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Kenduwa", getString(R.string.kenduwa)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Khaliajuri", getString(R.string.khaliajuri)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Modon", getString(R.string.modon)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Khaliajuri", getString(R.string.mohonganj)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Netrokona sodor", getString(R.string.netrokona_sadar)));
        subAreaModelList.add(new SubAreaModel("Netrokona", "Purbadhala", getString(R.string.purbadhala)));


        subAreaModelList.add(new SubAreaModel("Mymensingh", "Valuka", getString(R.string.valuka)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Bobaura", getString(R.string.bobaura)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Fulbariya", getString(R.string.fulbariya)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Goforgau", getString(R.string.goforgau)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Gouripur", getString(R.string.gouripur)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Haluaghat", getString(R.string.haluaghat)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Issorgonj", getString(R.string.issorganj)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Muktagacha", getString(R.string.muktagacha)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Mymensingh sodor", getString(R.string.mymensingh_sadar)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Nandail", getString(R.string.nandail)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Phulpur", getString(R.string.phulpur)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Tarakanda", getString(R.string.tarakanda)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Trishal", getString(R.string.trishal)));
        subAreaModelList.add(new SubAreaModel("Mymensingh", "Mymensingh city", getString(R.string.mymensingh_city)));


        subAreaModelList.add(new SubAreaModel("Jamalpur", "Bakshiganj", getString(R.string.bakshiganj)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Dewanganj", getString(R.string.dewanganj)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Islampur", getString(R.string.islampur)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Jamalpur sadar", getString(R.string.jamalpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Madarganj", getString(R.string.madarganj)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Melandho", getString(R.string.melandho)));
        subAreaModelList.add(new SubAreaModel("Jamalpur", "Sorisabaari", getString(R.string.sorisabari)));


        subAreaModelList.add(new SubAreaModel("Sherpur", "Jhinaigati", getString(R.string.jhinaigati)));
        subAreaModelList.add(new SubAreaModel("Sherpur", "Nalitabari", getString(R.string.nalitabari)));
        subAreaModelList.add(new SubAreaModel("Sherpur", "Nokla", getString(R.string.nokla)));
        subAreaModelList.add(new SubAreaModel("Sherpur", "Sherpur Sadar", getString(R.string.sherpur_sadar)));
        subAreaModelList.add(new SubAreaModel("Sherpur", "Sreebordi", getString(R.string.sreebordi)));


    }

}