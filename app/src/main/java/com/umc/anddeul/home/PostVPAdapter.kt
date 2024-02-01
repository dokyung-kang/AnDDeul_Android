package com.umc.anddeul.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.FragmentHomeUploadImageBinding

class PostVPAdapter(private val imageList: ArrayList<Uri>):
    RecyclerView.Adapter<PostVPAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: FragmentHomeUploadImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Uri) {
            binding.homeUploadImageIv.setImageURI(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding : FragmentHomeUploadImageBinding = FragmentHomeUploadImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    fun addImage(image: Uri) {
        // 이미 리스트에 있는지 확인
        if (!imageList.contains(image)) {
            imageList.add(image)
            notifyItemInserted(imageList.size - 1)
        }
    }
}