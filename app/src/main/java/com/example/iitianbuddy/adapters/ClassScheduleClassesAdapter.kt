package com.example.iitianbuddy.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.iitianbuddy.R
import com.example.iitianbuddy.models.Classes
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ClassScheduleClassesAdapter(val context: Context, val list: ArrayList<Classes>): RecyclerView.Adapter<ClassScheduleClassesAdapter.ViewHolder>() {


    companion object{
        var cr: Boolean = false
        var batch: String = "BSSE1"
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassScheduleClassesAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassScheduleClassesAdapter.ViewHolder, position: Int) {
        val model: Classes = list[position]
        holder.courseName.text = model.courseName
        holder.roomNumber.text = model.roomNumber
        holder.instructorName.text = model.instructorName
        val time: String = model.timeFrom + " - " + model.timeTo
        holder.time.text = time
        holder.classType.text = model.type
        if(model.type == "Regular"){
            holder.classType.background = ResourcesCompat.getDrawable(context.resources, R.drawable.regular_class_shape, context.theme)
//            Picasso.get().load(R.drawable.done).into(holder.currentState)
        }

        holder.itemView.setOnLongClickListener {
            if(cr){
                val dialog: Dialog = Dialog(context)
                dialog.setContentView(R.layout.edit_class_layout)


                val saveBtn: Button = dialog.findViewById(R.id.save)
                val cancelBtn: Button = dialog.findViewById(R.id.cancel)
                val spinner: Spinner = dialog.findViewById(R.id.spinner)
                val courseName: TextInputLayout = dialog.findViewById(R.id.course_code)
                val instructorName: TextInputLayout = dialog.findViewById(R.id.instructor_name)
                val roomNumber: TextInputLayout = dialog.findViewById(R.id.room_number)
                val fromTime: TextInputLayout = dialog.findViewById(R.id.from_time)
                val toTime: TextInputLayout = dialog.findViewById(R.id.to_time)

                courseName.editText!!.setText(model.courseName)
                instructorName.editText!!.setText(model.instructorName)
                roomNumber.editText!!.setText(model.roomNumber)
                fromTime.editText!!.setText(model.timeFrom)
                toTime.editText!!.setText(model.timeTo)


                val items: Array<String> = arrayOf("sun", "mon", "tue", "wed", "thu", "extra")
                val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
                spinner.adapter = adapter

                for(i: Int in items.indices){
                    if(items[i] == model.day){
                        spinner.setSelection(i)
                        break
                    }
                }

                cancelBtn.text = "Remove"
                saveBtn.text = "Update"
                dialog.show()


                cancelBtn.setOnClickListener {
                    val document: DocumentReference = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(model.day).document(model.courseName)
                    document.delete().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Class is removed!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        else{
                            Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }

                saveBtn.setOnClickListener {
                    val cName: String = courseName.editText!!.text.toString()
                    val iName: String = instructorName.editText!!.text.toString()
                    val rNumber: String = roomNumber.editText!!.text.toString()
                    val fTime: String = fromTime.editText!!.text.toString()
                    val tTime: String = toTime.editText!!.text.toString()
                    val day: String = spinner.selectedItem.toString()

                    val map: MutableMap<String, Any> = HashMap<String, Any>()
                    map["courseName"] = cName
                    map["instructorName"] = iName
                    map["roomNumber"] = rNumber
                    map["timeFrom"] = fTime
                    map["timeTo"] = tTime
                    map["type"] = "Regular"
                    map["day"] = day

                    if(day == model.day){
                        if(model.courseName == cName){
                            val document: DocumentReference = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(day).document(model.courseName)
                            document.update(map).addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(context, "Class Updated!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                                else{
                                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        }
                        else{
                            var document: DocumentReference = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(day).document(cName)
                            document.set(map).addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(context, "Class Updated!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    document = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(day).document(model.courseName)
                                    document.delete()
                                }
                                else{
                                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        }
                    }
                    else{
                        var document: DocumentReference = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(day).document(cName)
                        document.set(map).addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(context, "Class Updated!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                document = FirebaseFirestore.getInstance().collection("Classes").document(batch).collection(model.day).document(model.courseName)
                                document.delete()
                            }
                            else{
                                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                        }
                    }

                }
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val courseName: TextView = itemView.findViewById(R.id.courseName)
        val roomNumber: TextView = itemView.findViewById(R.id.roomNumber)
        val instructorName: TextView = itemView.findViewById(R.id.instructorName)
        val classType: TextView = itemView.findViewById(R.id.class_type)
        val time: TextView = itemView.findViewById(R.id.classTime)
        val currentState: CircleImageView = itemView.findViewById(R.id.current_state)
    }
}