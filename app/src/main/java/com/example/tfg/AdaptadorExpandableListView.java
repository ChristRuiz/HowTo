package com.example.tfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class AdaptadorExpandableListView extends BaseExpandableListAdapter {

    private Context context;
    private Map<Integer, Paso> expandableListPasos;
    private Integer[] listPasos;

    /**
     * Constructor
     * @param context
     * @param expandableListPasos
     */
    public AdaptadorExpandableListView(Context context, Map<Integer, Paso> expandableListPasos) {
        this.context = context;
        this.expandableListPasos = expandableListPasos;
        this.listPasos = expandableListPasos.keySet().toArray(new Integer[0]);
    }

    /**
     * Getter del numero de grupos que hay
     * @return
     */
    @Override
    public int getGroupCount() {
        return listPasos.length;
    }

    /**
     * Getter del numero de hijos que hay dentro del grupo
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * Getter para tener el grupo
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return listPasos[groupPosition];
    }

    /**
     * Getter para tener el hijo
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableListPasos.get(this.listPasos[groupPosition]);
    }

    /**
     * Getter para tener el id del grupo
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Getter para tener el id del hijo
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Comprueba si tiene ids estables
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Getter para obtener la vista del grupo
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //Obtenemos el numero del paso actual
        Integer numero_paso = (Integer) getGroup(groupPosition);

        if (convertView == null) {
            //Indicamos a la vista que layout tiene que cargar
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group,parent,false);

        }

        //Mostramos el paso actual
        TextView nombre_paso = convertView.findViewById(R.id.nombre_paso);
        nombre_paso.setText("Paso "+numero_paso);

        return convertView;
    }

    /**
     * Getter para obtener la vista del hijo
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        //Obtenemos los datos del paso
        final Paso paso = (Paso) getChild(groupPosition, childPosition);

        if (convertView == null) {
            //Indicamos a la vista que layout tiene que cargar
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        }

        TextView titulo_paso = convertView.findViewById(R.id.titulo_paso);
        TextView descripcion_paso = convertView.findViewById(R.id.descripcion_paso);
        ImageView foto_paso = convertView.findViewById(R.id.foto_paso);

        //Rellenamos los textviews
        titulo_paso.setText(paso.getTitulo());
        descripcion_paso.setText(paso.getComentario());

        if(paso.getImagen()!=null){
            //Si hay imagen, se carga la imagen y se pone visible
            foto_paso.setVisibility(View.VISIBLE);
            Picasso.get().load(paso.getImagen()).placeholder(R.drawable.placeholder_image).fit().centerCrop().into(foto_paso);

        } else {
            //Si no hay imagen, no se dedica ningun espacio en la vista al imageview
            foto_paso.setVisibility(View.GONE);
        }


        return convertView;
    }

    /**
     * Indiciamos si se puede seleccionar el hijo
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
