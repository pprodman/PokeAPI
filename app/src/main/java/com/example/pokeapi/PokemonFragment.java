package com.example.pokeapi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokeapi.databinding.FragmentPokemonBinding;
import com.example.pokeapi.databinding.ViewholderContenidoBinding;
import com.example.pokeapi.models.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class PokemonFragment extends Fragment {

    private FragmentPokemonBinding binding;
    private PokemonViewModel viewModel;
    private ContenidosAdapter adapter;

    // Inicializa el fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPokemonBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // Configura la vista
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PokemonViewModel.class);
        adapter = new ContenidosAdapter();
        binding.contenidos.setAdapter(adapter);

        viewModel.getPokemonListLiveData().observe(getViewLifecycleOwner(), pokemonList -> {
            if (pokemonList != null) {
                adapter.setPokemonList(pokemonList);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e("PokemonFragment", error);
                // You might want to show this error to the user
            }
        });

        // SearchView listener
        binding.texto.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchPokemon(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.searchPokemon(newText);
                return true;
            }
        });

        // Fetch initial list of Pokemon (sabemos que existen 1302 Pokemon segun la API)
        viewModel.fetchPokemonList(1302, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ADAPTADOR DE LA LISTA DE CONTENIDOS
    static class ContenidosAdapter extends RecyclerView.Adapter<ContenidoViewHolder> {

        private List<Pokemon> pokemonList = new ArrayList<>();

        // Crea el ViewHolder
        @NonNull
        @Override
        public ContenidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContenidoViewHolder(ViewholderContenidoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        // Bind datos en el ViewHolder
        @Override
        public void onBindViewHolder(@NonNull ContenidoViewHolder holder, int position) {
            Pokemon pokemon = pokemonList.get(position);
            holder.binding.name.setText(pokemon.getName());
            holder.binding.height.setText("Altura: " + pokemon.getHeight());
            holder.binding.weight.setText("Peso: " + pokemon.getWeight());
        }

        // Devuelve el número de elementos en la lista
        @Override
        public int getItemCount() {
            return pokemonList.size();
        }

        // Actualiza la lista de Pokémon
        void setPokemonList(List<Pokemon> pokemonList) {
            this.pokemonList = pokemonList;
            notifyDataSetChanged();
        }
    }

    // VIEWHOLDER DE LA LISTA DE CONTENIDOS
    static class ContenidoViewHolder extends RecyclerView.ViewHolder {
        ViewholderContenidoBinding binding;

        public ContenidoViewHolder(@NonNull ViewholderContenidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

