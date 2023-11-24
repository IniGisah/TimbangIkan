package my.id.luii.timbangikan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class nelayanListAdapter extends BaseAdapter {

    Context context;
    private final ArrayList<String> nama;
    private final ArrayList<String> nik;

    public nelayanListAdapter(Context context, ArrayList<String> nama, ArrayList<String> nik){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.nama = nama;
        this.nik = nik;
    }

    @Override
    public int getCount() {
        return nama.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_nelayan, parent, false);
            viewHolder.txtNama = (TextView) convertView.findViewById(R.id.nelayanname);
            viewHolder.txtNik = (TextView) convertView.findViewById(R.id.nelayanid);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtNama.setText(nama.get(position));
        viewHolder.txtNik.setText(nik.get(position));

        return convertView;
    }

    private static class ViewHolder {

        TextView txtNama;
        TextView txtNik;

    }

}
