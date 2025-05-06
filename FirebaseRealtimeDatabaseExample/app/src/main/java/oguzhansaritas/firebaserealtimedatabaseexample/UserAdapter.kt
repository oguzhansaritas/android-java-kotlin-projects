package baristuzemen.firebaserealtimedatabaseexample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserAdapter(context: Context, users: ArrayList<User>) : ArrayAdapter<User>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val user = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_view_record, parent, false)
        }

        val text1 = convertView!!.findViewById<View>(R.id.id) as TextView
        val text2 = convertView.findViewById<View>(R.id.name) as TextView
        val text3 = convertView.findViewById<View>(R.id.address) as TextView

        text1.text = user!!.counter
        text2.text = user.name
        text3.text = user.address

        return convertView
    }
}