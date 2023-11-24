package my.id.luii.timbangikan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ikanListAdapter extends BaseAdapter {

    Context context;
    private final ArrayList<String> nama;
    private final ArrayList<String> grade;
    private final ArrayList<Integer> harga;

    public ikanListAdapter(Context context, ArrayList<String> nama, ArrayList<String> grade, ArrayList<Integer> harga){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.nama = nama;
        this.grade = grade;
        this.harga = harga;
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
            convertView = inflater.inflate(R.layout.list_ikan, parent, false);
            viewHolder.txtNama = (TextView) convertView.findViewById(R.id.ikaname);
            viewHolder.txtGrade = (TextView) convertView.findViewById(R.id.ikangrade);
            viewHolder.txtHarga = (TextView) convertView.findViewById(R.id.ikanprice);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtNama.setText(nama.get(position));
        viewHolder.txtGrade.setText(grade.get(position));
        viewHolder.txtHarga.setText(harga.get(position));

        return convertView;
    }

    private static class ViewHolder {

        TextView txtNama;
        TextView txtGrade;
        TextView txtHarga;

    }

}
