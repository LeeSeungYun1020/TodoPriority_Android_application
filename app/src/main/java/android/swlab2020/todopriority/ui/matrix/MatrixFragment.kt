package android.swlab2020.todopriority.ui.matrix

import android.os.Bundle
import android.swlab2020.todopriority.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MatrixFragment : Fragment() {

    private lateinit var matrixViewModel: MatrixViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        matrixViewModel = ViewModelProvider(this)[MatrixViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_matrix, container, false)
        val textView: TextView = root.findViewById(R.id.text_matrix)
        matrixViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
