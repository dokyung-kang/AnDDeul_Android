package com.umc.anddeul.postbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentPostboxBinding
import com.umc.anddeul.databinding.ItemCalendarBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class PostboxFragment : Fragment() {
    private lateinit var binding: FragmentPostboxBinding
    private lateinit var postAdapter: LetterAdapter
    private var currentStartOfWeek: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostboxBinding.inflate(inflater, container, false)

        //// 화분 키우기 페이지로 이동
        binding.gotoPotBtn.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm, PotFragment())
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
        }


        //// 달력

        setWeek(currentStartOfWeek)

        // 저번주
        binding.beforeBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }
        
        // 다음주
        binding.afterBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.plusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        //// 편지 리스트
        postAdapter = LetterAdapter()

        // 테스트용 더미 데이터
        val dummyPosts = listOf(
            Letter(1, "아티", 0, "어쩌구저쩌구"),
            Letter(2, "도라", 0, "어쩌구저쩌구"),
            Letter(3, "지나", 0, "어쩌구저쩌구"),
            Letter(4, "율", 1, "음성 메세지가 도착했습니다."),
            Letter(5, "도도", 1, "음성 메세지가 도착했습니다."),
            Letter(6, "훈", 1, "음성 메세지가 도착했습니다."),
            Letter(7, "빈온", 0, "어쩌구저쩌구"),
            Letter(8, "세흐", 0, "어쩌구저쩌구"),
        )

        postAdapter.letters = dummyPosts


        //// 편지 보기(팝업)
        val onClickListener = object: LetterAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
                postPopupFragment.show(dummyPosts[pos])
            }
        }
        postAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetters.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvLetters.adapter = postAdapter


        //// 편지 작성 (음성)
        binding.voiceIv.setOnClickListener{
            if(binding.letterEt.text.toString().isEmpty()){   // 작성된 텍스트 없을 때
                val recordPopupFragment = RecordPopupFragment(requireContext())
                recordPopupFragment.show()
            } else {    // 작성된 텍스트 있을 때
                val dialogFragment = DialogLetterFragment(requireContext())
                dialogFragment.show("voice")
            }
        }


        //// 편지 작성 (텍스트)
        binding.mailIv.setOnClickListener{
            // 편지 보내는 기능 추가

            //텍스트 초기화
            binding.letterEt.setText("")
        }

        return binding.root
    }

    private fun setWeek(startOfWeek: LocalDate) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

        for (i in 1..7) {
            val currentDateForDay = nearestMonday.plusDays(i.toLong() - 1)
            val dateTextView = when (i) {
                1 -> binding.date1
                2 -> binding.date2
                3 -> binding.date3
                4 -> binding.date4
                5 -> binding.date5
                6 -> binding.date6
                7 -> binding.date7
                else -> null
            }

            dateTextView?.text = formatDate(currentDateForDay)

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                Toast.makeText(requireContext(), "Selected date: $currentDateForDay", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }

    private fun setClickListener(date: LocalDate, textView: TextView) {
        textView.setOnClickListener {
            textView.setBackgroundResource(R.drawable.calendar_circle)
            showToast(date)
        }
    }

    private fun showToast(date: LocalDate) {
        Toast.makeText(requireContext(), "Selected date: $date", Toast.LENGTH_SHORT).show()
    }
}
