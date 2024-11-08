package com.sparkmembership.sparkowner.presentation.ui.invoice.InvoiceDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentInvoiceDetailBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity

class InvoiceDetailFragment : Fragment() {
    lateinit var binding: FragmentInvoiceDetailBinding
    private var invoiceId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceDetailBinding.inflate(inflater, container, false)
        arguments?.let {
            invoiceId = it.getInt("invoiceId")
        }
        return binding.root
    }
    private fun setToolBar() {
        (activity as MainActivity).showToolbar()

        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.invoices_details,invoiceId),
            toolbarColor = android.R.color.transparent,
            showBackButton = false,
            icons = listOf(

            ),
            onBackPress = {
            }
        )

    }

}