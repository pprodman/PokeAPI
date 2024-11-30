package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pokeapi.models.Pokemon;
import com.example.pokeapi.models.PokemonList;
import com.example.pokeapi.poqueapi.PokeApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonViewModel extends ViewModel {

    private final MutableLiveData<List<Pokemon>> pokemonListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private List<Pokemon> allPokemonList = new ArrayList<>();

    private final PokeApiService service;

    public PokemonViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PokeApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(PokeApiService.class);
    }

    public LiveData<List<Pokemon>> getPokemonListLiveData() {
        return pokemonListLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Captura la lista de Pokémon
    public void fetchPokemonList(int limit, int offset) {
        service.getPokemonList(limit, offset).enqueue(new Callback<PokemonList>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPokemonList = response.body().getResults();
                    pokemonListLiveData.setValue(allPokemonList);
                    for (Pokemon pokemon : allPokemonList) {
                        fetchPokemonDetails(pokemon.getName());
                    }
                } else {
                    errorLiveData.setValue("Error: No se pudo obtener la lista de Pokémon.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
                errorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }

    // Filtra la lista de Pokémon
    public void searchPokemon(String query) {
        if (query.isEmpty()) {
            pokemonListLiveData.setValue(allPokemonList);
        } else {
            List<Pokemon> filteredList = new ArrayList<>();
            for (Pokemon pokemon : allPokemonList) {
                if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pokemon);
                }
            }
            pokemonListLiveData.setValue(filteredList);
        }
    }

    // Actualiza los detalles del Pokémon
    public void fetchPokemonDetails(String name) {
        service.getPokemonByName(name).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(@NonNull Call<Pokemon> call, @NonNull Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatePokemonDetails(response.body());
                } else {
                    errorLiveData.setValue("Error: No se pudo obtener los detalles del Pokémon.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pokemon> call, @NonNull Throwable t) {
                errorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }

    // Actualiza los detalles del Pokémon
    private void updatePokemonDetails(Pokemon updatedPokemon) {
        List<Pokemon> currentList = pokemonListLiveData.getValue();
        if (currentList != null) {
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).getName().equals(updatedPokemon.getName())) {
                    currentList.set(i, updatedPokemon);
                    break;
                }
            }
            pokemonListLiveData.setValue(currentList);
        }
    }
}

