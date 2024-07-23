package com.umc.anddeul.checklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.toast.AnddeulToast
import com.umc.anddeul.databinding.ItemChecklistBinding
import com.umc.anddeul.home.HomeFragment
import com.umc.anddeul.home.PostWriteActivity
import java.io.File


class ChecklistRVAdapter(private val context : Context) : RecyclerView.Adapter<ChecklistRVAdapter.ViewHolder>() {
    var checklist: List<Checklist>? = null
    lateinit var file : File
    lateinit var currentChecklist : Checklist
    val REQUEST_CODE = 200

    override fun getItemCount(): Int {
        return checklist?.size ?: 0
    }

    fun setChecklistData(checklist: List<Checklist>) {
        this.checklist = checklist
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChecklistRVAdapter.ViewHolder {
        val binding: ItemChecklistBinding = ItemChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistRVAdapter.ViewHolder, position: Int) {

        //체크리스트 리사이클러뷰 연결
        holder.bind(checklist!!.get(position))

        //카메라 앱 연동 함수
        holder.binding.checkliBtnCamera.setOnClickListener {
            //함수 호출
            currentChecklist = checklist!!.get(position)

            if (currentChecklist.complete == 0) {
                AnddeulToast.createToast(context, "체크리스트 달성 전에는 인증샷을 추가할 수 없습니다.")?.show()
            }
            else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                intent.putExtra("checkId", currentChecklist.check_idx)
                (context as Activity).startActivityForResult(intent, REQUEST_CODE)

            }
        }

        holder.binding.checkliBtnChecking.setOnClickListener {
            ChecklistService(context).completeApi(checklist!!.get(position))
            checking(holder.binding)
        }

        holder.binding.checkliBtnChecked.setOnClickListener {
            ChecklistService(context).completeApi(checklist!!.get(position))
            checked(holder.binding)
        }

    }

    fun checking(binding : ItemChecklistBinding) {
        binding.checkliBtnChecking.visibility = View.INVISIBLE
        binding.checkliBtnChecked.visibility = View.VISIBLE
        binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
        binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
    }

    fun checked(binding : ItemChecklistBinding) {
        binding.checkliBtnChecking.visibility = View.VISIBLE
        binding.checkliBtnChecked.visibility = View.INVISIBLE
        binding.checkliTvWriter.setTextColor(Color.parseColor("#261710"))
        binding.checkliTvChecklist.setTextColor(Color.parseColor("#261710"))
    }

    inner class ViewHolder(val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist: Checklist) {
            binding.checkliTvChecklist.text = checklist?.content
            binding.checkliTvWriter.text = checklist?.sender + "님이 남기셨습니다."
            if (checklist.picture != null) {
                binding.checkliIvPhoto.visibility = View.VISIBLE
                val loadCheckImg = LoadCheckImg(binding.checkliIvPhoto)
                loadCheckImg.execute(checklist.picture)
            } else {
                binding.checkliIvPhoto.visibility = View.GONE
            }
            if (checklist.complete == 1) {
                binding.checkliBtnChecking.visibility = View.INVISIBLE
                binding.checkliBtnChecked.visibility = View.VISIBLE
                binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
                binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
            }
        }
    }
}
